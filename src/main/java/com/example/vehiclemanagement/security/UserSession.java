package com.example.vehiclemanagement.security;

/**
 * 用户会话类
 * 用于存储用户登录后的会话信息
 */
public class UserSession {
    /** 用户名 */
    private final String username;
    /** 用户角色 */
    private final String role;
    /** JWT 令牌 */
    private final String token;

    /**
     * 构造函数
     * @param username 用户名
     * @param role 用户角色
     * @param token JWT 令牌
     */
    public UserSession(String username, String role, String token) {
        this.username = username;
        this.role = role;
        this.token = token;
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
}
