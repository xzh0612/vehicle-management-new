package com.example.vehiclemanagement.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 注册请求类
 * 用于接收用户注册信息
 */
public class RegisterRequest {
    /** 用户名 */
    @NotBlank
    private String username;
    /** 密码 */
    @NotBlank
    private String password;
    /** 用户角色，默认为 USER */
    private String role = "USER";

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
     * 获取密码
     * @return 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password;
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
