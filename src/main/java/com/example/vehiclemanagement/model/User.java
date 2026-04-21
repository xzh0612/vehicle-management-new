package com.example.vehiclemanagement.model;

/**
 * 用户模型类
 * 用于表示系统用户信息
 */
public class User {
    /** 用户名 */
    private String username;
    /** 密码哈希值 */
    private String passwordHash;
    /** 用户角色 */
    private String role;

    /**
     * 获取用户名
     * @return 用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名
     * @param username 用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取密码哈希值
     * @return 密码哈希值
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * 设置密码哈希值
     * @param passwordHash 密码哈希值
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * 获取用户角色
     * @return 用户角色
     */
    public String getRole() {
        return role;
    }

    /**
     * 设置用户角色
     * @param role 用户角色
     */
    public void setRole(String role) {
        this.role = role;
    }
}
