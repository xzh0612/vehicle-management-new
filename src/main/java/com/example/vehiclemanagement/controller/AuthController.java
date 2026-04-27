package com.example.vehiclemanagement.controller;

import com.example.vehiclemanagement.dto.ApiResponse;
import com.example.vehiclemanagement.dto.AuthResponse;
import com.example.vehiclemanagement.dto.LoginRequest;
import com.example.vehiclemanagement.dto.RegisterRequest;
import com.example.vehiclemanagement.security.UserSession;
import com.example.vehiclemanagement.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

/**
 * 认证控制器，负责暴露注册、登录、登出与当前用户查询接口。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    /** 认证服务 */
    private final AuthService authService;

    /**
     * 构造函数
     * @param authService 认证服务
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户注册
     * @param request 注册请求
     * @return 注册结果
     */
    @PostMapping("/register")
    public ApiResponse<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ApiResponse.success(null, "注册成功");
    }

    /**
     * 管理员创建用户
     * @param authorization 认证令牌
     * @param request 注册请求
     * @return 创建结果
     */
    @PostMapping("/register/admin")
    public ApiResponse<Void> registerByAdmin(@RequestHeader("Authorization") String authorization,
                                             @Valid @RequestBody RegisterRequest request) {
        authService.registerByAdmin(request, authorization);
        return ApiResponse.success(null, "创建用户成功");
    }

    /**
     * 用户登录
     * @param request 登录请求
     * @return 登录响应，包含令牌
     */
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request), "登录成功");
    }

    /**
     * 用户登出
     * @param authorization 认证令牌
     * @return 登出结果
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader("Authorization") String authorization) {
        authService.logout(authorization);
        return ApiResponse.success(null, "退出登录成功");
    }

    /**
     * 获取当前用户信息
     * @param authorization 认证令牌
     * @return 当前用户信息
     */
    @GetMapping("/me")
    public ApiResponse<Map<String, Object>> me(@RequestHeader("Authorization") String authorization) {
        UserSession session = authService.requireUser(authorization);
        return ApiResponse.success(Map.of("username", session.getUsername(), "role", session.getRole()));
    }
}
