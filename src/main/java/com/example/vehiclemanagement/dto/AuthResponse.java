package com.example.vehiclemanagement.dto;

/**
 * 认证响应类
 * 用于返回登录成功后的用户信息和令牌
 */
public class AuthResponse {
    /** 用户名 */
    private final String username;
    /** 用户角色 */
    private final String role;
    /** JWT 令牌 */
    private final String token;
    /** 令牌过期时间（秒） */
    private final long expireSeconds;

    /**
     * 构造函数
     * @param username 用户名
     * @param role 用户角色
     * @param token JWT 令牌
     * @param expireSeconds 令牌过期时间（秒）
     */
    public AuthResponse(String username, String role, String token, long expireSeconds) {
        this.username = username;
        this.role = role;
        this.token = token;
        this.expireSeconds = expireSeconds;
    }

    /**
     * 获取用户名
     * @return 用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 获取用户角色
     * @return 用户角色
     */
    public String getRole() {
        return role;
    }

    /**
     * 获取 JWT 令牌
     * @return JWT 令牌
     */
    public String getToken() {
        return token;
    }

    /**
     * 获取令牌过期时间
     * @return 令牌过期时间（秒）
     */
    public long getExpireSeconds() {
        return expireSeconds;
    }
}
