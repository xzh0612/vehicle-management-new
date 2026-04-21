package com.example.vehiclemanagement.util;

import com.example.vehiclemanagement.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

/**
 * JWT 工具类，用于生成和解析 JWT 令牌。
 */
@Component
public class JwtUtil {
    /** JWT 配置属性 */
    private final JwtProperties properties;

    /**
     * 构造函数
     * @param properties JWT 配置属性
     */
    public JwtUtil(JwtProperties properties) {
        this.properties = properties;
    }

    /**
     * 生成 JWT 令牌
     * @param username 用户名
     * @param role 角色
     * @return JWT 令牌
     */
    public String generateToken(String username, String role) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(properties.getExpireSeconds());
        return Jwts.builder()
                .issuer(properties.getIssuer())
                .subject(username)
                .claim("role", role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(signingKey())
                .compact();
    }

    /**
     * 解析 JWT 令牌
     * @param token JWT 令牌
     * @return 令牌中的声明
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 获取令牌过期时间（秒）
     * @return 过期时间（秒）
     */
    public long getExpireSeconds() {
        return properties.getExpireSeconds();
    }

    /**
     * 获取签名密钥
     * @return 签名密钥
     */
    private SecretKey signingKey() {
        String secret = properties.getSecret();
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("JWT密钥长度必须至少32个字符");
        }
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
