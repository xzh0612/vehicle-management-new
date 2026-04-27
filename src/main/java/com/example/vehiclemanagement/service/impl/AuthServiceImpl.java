package com.example.vehiclemanagement.service.impl;

import com.example.vehiclemanagement.dto.AuthResponse;
import com.example.vehiclemanagement.dto.LoginRequest;
import com.example.vehiclemanagement.dto.RegisterRequest;
import com.example.vehiclemanagement.exception.BadRequestException;
import com.example.vehiclemanagement.exception.UnauthorizedException;
import com.example.vehiclemanagement.model.User;
import com.example.vehiclemanagement.repository.UserRepository;
import com.example.vehiclemanagement.security.UserSession;
import com.example.vehiclemanagement.service.AuthService;
import com.example.vehiclemanagement.util.JwtUtil;
import com.example.vehiclemanagement.util.PasswordUtil;
import com.example.vehiclemanagement.util.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现类，处理用户注册、登录和基于 Redis 的会话校验。
 */
@Service
public class AuthServiceImpl implements AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    /** 用户仓库 */
    private final UserRepository userRepository;
    /** JWT 工具类 */
    private final JwtUtil jwtUtil;
    /** Redis 模板 */
    private final StringRedisTemplate redisTemplate;

    /** Token 前缀 */
    @Value("${security.token-prefix:login:token:}")
    private String tokenPrefix;

    /** Redis 不可用时的本地会话兜底。 */
    private final Map<String, LocalSession> localSessions = new ConcurrentHashMap<>();

    /**
     * 构造函数
     * @param userRepository 用户仓库
     * @param jwtUtil JWT 工具类
     * @param redisTemplate Redis 模板
     */
    public AuthServiceImpl(UserRepository userRepository, JwtUtil jwtUtil, StringRedisTemplate redisTemplate) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 用户注册
     * @param request 注册请求
     */
    @Override
    public void register(RegisterRequest request) {
        try {
            String username = normalize(request.getUsername());
            if (userRepository.findByUsername(username).isPresent()) {
                throw new BadRequestException("用户名已存在");
            }

            long userCount = userRepository.countUsers();
            UserRole requestedRole = normalizeRole(request.getRole());
            UserRole finalRole = UserRole.USER;
            if (userCount == 0 && requestedRole.isAdmin()) {
                finalRole = UserRole.ADMIN;
            }

            User user = new User();
            user.setUsername(username);
            user.setPasswordHash(PasswordUtil.sha256(request.getPassword()));
            user.setRole(finalRole.name());
            userRepository.save(user);
        } catch (IOException e) {
            throw new IllegalStateException("注册失败: " + e.getMessage(), e);
        }
    }

    /**
     * 管理员创建用户
     * @param request 注册请求
     * @param authorization 认证令牌
     */
    @Override
    public void registerByAdmin(RegisterRequest request, String authorization) {
        UserSession operator = requireUser(authorization);
        ensureAdmin(operator);

        try {
            String username = normalize(request.getUsername());
            if (userRepository.findByUsername(username).isPresent()) {
                throw new BadRequestException("用户名已存在");
            }

            User user = new User();
            user.setUsername(username);
            user.setPasswordHash(PasswordUtil.sha256(request.getPassword()));
            user.setRole(normalizeRole(request.getRole()).name());
            userRepository.save(user);
        } catch (IOException e) {
            throw new IllegalStateException("管理员创建用户失败: " + e.getMessage(), e);
        }
    }

    /**
     * 用户登录
     * @param request 登录请求
     * @return 登录响应，包含令牌
     */
    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            User user = userRepository.findByUsername(normalize(request.getUsername()))
                    .orElseThrow(() -> new UnauthorizedException("用户名或密码错误"));
            if (!PasswordUtil.matchesStoredPassword(user.getPasswordHash(), request.getPassword())) {
                throw new UnauthorizedException("用户名或密码错误");
            }

            String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
            String key = redisKey(token);
            String value = user.getUsername() + "|" + user.getRole();
            saveSession(key, value, jwtUtil.getExpireSeconds());
            return new AuthResponse(user.getUsername(), user.getRole(), token, jwtUtil.getExpireSeconds());
        } catch (IOException e) {
            throw new IllegalStateException("登录失败: " + e.getMessage(), e);
        }
    }

    /**
     * 用户登出
     * @param authorization 认证令牌
     */
    @Override
    public void logout(String authorization) {
        String token = extractToken(authorization);
        String key = redisKey(token);
        localSessions.remove(key);
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.warn("Redis 删除会话失败，已从本地会话缓存移除: {}", e.getMessage());
        }
    }

    /**
     * 校验用户登录态
     * @param authorization 认证令牌
     * @return 用户会话信息
     */
    @Override
    public UserSession requireUser(String authorization) {
        String token = extractToken(authorization);
        Claims claims;
        try {
            claims = jwtUtil.parseToken(token);
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizedException("Token无效或已过期");
        }

        String username = claims.getSubject();
        String role = String.valueOf(claims.get("role"));
        String cached = readSession(redisKey(token));
        if (cached == null) {
            throw new UnauthorizedException("登录态已失效，请重新登录");
        }

        String expected = username + "|" + role;
        if (!expected.equals(cached)) {
            throw new UnauthorizedException("登录态校验失败");
        }
        return new UserSession(username, normalizeRole(role).name(), token);
    }

    /**
     * 校验管理员登录态
     * @param authorization 认证令牌
     * @return 用户会话信息
     */
    @Override
    public UserSession requireAdmin(String authorization) {
        UserSession session = requireUser(authorization);
        ensureAdmin(session);
        return session;
    }

    /**
     * 确保用户是管理员
     * @param session 用户会话
     */
    private void ensureAdmin(UserSession session) {
        if (!UserRole.ADMIN.name().equals(session.getRole())) {
            throw new UnauthorizedException("需要管理员权限");
        }
    }

    /**
     * 标准化字符串
     * @param value 输入字符串
     * @return 标准化后的字符串
     */
    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.trim();
    }

    /**
     * 标准化角色
     * @param role 角色字符串
     * @return 标准化后的角色
     */
    private UserRole normalizeRole(String role) {
        String normalized = normalize(role).toUpperCase(Locale.ROOT);
        if (UserRole.ADMIN.name().equals(normalized)) {
            return UserRole.ADMIN;
        }
        return UserRole.USER;
    }

    /**
     * 从授权头中提取令牌
     * @param authorization 授权头
     * @return 令牌
     */
    private String extractToken(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new UnauthorizedException("缺少Token");
        }
        String value = authorization.trim();
        if (value.regionMatches(true, 0, "Bearer ", 0, 7)) {
            value = value.substring(7).trim();
        }
        if (value.isBlank()) {
            throw new UnauthorizedException("Token无效");
        }
        return value;
    }

    /**
     * 生成 Redis 键
     * @param token 令牌
     * @return Redis 键
     */
    private String redisKey(String token) {
        return tokenPrefix + token;
    }

    private void saveSession(String key, String value, long expireSeconds) {
        long expireAt = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(expireSeconds);
        localSessions.put(key, new LocalSession(value, expireAt));
        try {
            redisTemplate.opsForValue().set(key, value, expireSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("Redis 不可用，当前登录态已退回本地内存会话: {}", e.getMessage());
        }
    }

    private String readSession(String key) {
        try {
            String redisValue = redisTemplate.opsForValue().get(key);
            if (redisValue != null) {
                return redisValue;
            }
        } catch (Exception e) {
            log.warn("Redis 读取会话失败，尝试使用本地内存会话: {}", e.getMessage());
        }

        LocalSession localSession = localSessions.get(key);
        if (localSession == null) {
            return null;
        }
        if (localSession.expireAtMillis() < System.currentTimeMillis()) {
            localSessions.remove(key);
            return null;
        }
        return localSession.value();
    }

    private record LocalSession(String value, long expireAtMillis) {
    }
}
