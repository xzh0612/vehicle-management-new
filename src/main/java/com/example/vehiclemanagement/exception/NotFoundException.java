package com.example.vehiclemanagement.exception;

/**
 * 资源不存在异常类
 * 当请求的资源不存在时抛出
 */
public class NotFoundException extends RuntimeException {
    /**
     * 构造函数
     * @param message 异常信息
     */
    public NotFoundException(String message) {
        super(message);
    }
}
