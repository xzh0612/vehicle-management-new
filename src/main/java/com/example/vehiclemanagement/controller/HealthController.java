package com.example.vehiclemanagement.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 健康检查控制器
 * 用于检查系统是否正常运行
 */
@RestController
@RequestMapping("/api")
public class HealthController {
    /**
     * 健康检查端点
     * @return 系统状态
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of("status", "UP");
    }
}
