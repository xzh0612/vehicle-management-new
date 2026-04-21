package com.example.vehiclemanagement.exception;

/**
 * 请求参数异常类
 * 当请求参数不合法时抛出
 */
public class BadRequestException extends RuntimeException {
    /**
     * 构造函数
     * @param message 异常信息
     */
    public BadRequestException(String message) {
        super(message);
    }
}
