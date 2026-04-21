package com.example.vehiclemanagement.exception;

/**
 * 未授权异常类
 * 当用户未授权访问资源时抛出
 */
public class UnauthorizedException extends RuntimeException {
    /**
     * 构造函数
     * @param message 异常信息
     */
    public UnauthorizedException(String message) {
        super(message);
    }
}
