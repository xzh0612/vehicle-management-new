package com.example.vehiclemanagement.service;

import com.example.vehiclemanagement.dto.AuthResponse;
import com.example.vehiclemanagement.dto.LoginRequest;
import com.example.vehiclemanagement.dto.RegisterRequest;
import com.example.vehiclemanagement.security.UserSession;

/**
 * 认证服务接口，负责注册、登录、登出与登录态校验。
 */
public interface AuthService {
    /**
     * 用户注册
     * @param request 注册请求
     */
    void register(RegisterRequest request);

    /**
     * 管理员创建用户
     * @param request 注册请求
     * @param authorization 认证令牌
     */
    void registerByAdmin(RegisterRequest request, String authorization);

    /**
     * 用户登录
     * @param request 登录请求
     * @return 登录响应，包含令牌
     */
    AuthResponse login(LoginRequest request);

    /**
     * 用户登出
     * @param authorization 认证令牌
     */
    void logout(String authorization);

    /**
     * 校验用户登录态
     * @param authorization 认证令牌
     * @return 用户会话信息
     */
    UserSession requireUser(String authorization);

    /**
     * 校验管理员登录态
     * @param authorization 认证令牌
     * @return 用户会话信息
     */
    UserSession requireAdmin(String authorization);
}
