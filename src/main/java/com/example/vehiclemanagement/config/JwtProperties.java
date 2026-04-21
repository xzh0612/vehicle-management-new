package com.example.vehiclemanagement.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置属性类
 * 从 application.yml 中读取 JWT 相关配置
 */
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    /** JWT 签发者 */
    private String issuer;
    /** JWT 密钥 */
    private String secret;
    /** JWT 过期时间（秒） */
    private long expireSeconds;

    /**
     * 获取 JWT 签发者
     * @return JWT 签发者
     */
    public String getIssuer() {
        return issuer;
    }

    /**
     * 设置 JWT 签发者
     * @param issuer JWT 签发者
     */
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    /**
     * 获取 JWT 密钥
     * @return JWT 密钥
     */
    public String getSecret() {
        return secret;
    }

    /**
     * 设置 JWT 密钥
     * @param secret JWT 密钥
     */
    public void setSecret(String secret) {
        this.secret = secret;
    }

    /**
     * 获取 JWT 过期时间
     * @return JWT 过期时间（秒）
     */
    public long getExpireSeconds() {
        return expireSeconds;
    }

    /**
     * 设置 JWT 过期时间
     * @param expireSeconds JWT 过期时间（秒）
     */
    public void setExpireSeconds(long expireSeconds) {
        this.expireSeconds = expireSeconds;
    }
}
