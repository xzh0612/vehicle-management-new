package com.example.vehiclemanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 * 统一处理系统中的各种异常
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理请求参数异常
     * @param ex 异常对象
     * @return 错误响应
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(BadRequestException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * 处理未授权异常
     * @param ex 异常对象
     * @return 错误响应
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorized(UnauthorizedException ex) {
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    /**
     * 处理资源不存在异常
     * @param ex 异常对象
     * @return 错误响应
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * 处理参数校验异常
     * @param ex 异常对象
     * @return 错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage())
                .orElse("参数校验失败");
        return build(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * 处理通用异常
     * @param ex 异常对象
     * @return 错误响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    /**
     * 构建错误响应
     * @param status HTTP 状态码
     * @param message 错误信息
     * @return 响应实体
     */
    private ResponseEntity<Map<String, Object>> build(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", status.value());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
