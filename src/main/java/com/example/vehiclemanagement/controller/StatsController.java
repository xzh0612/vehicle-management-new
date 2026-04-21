package com.example.vehiclemanagement.controller;

import com.example.vehiclemanagement.service.AuthService;
import com.example.vehiclemanagement.service.StatsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

/**
 * 统计控制器，负责统计查询与统计结果上传接口。
 */
@RestController
@RequestMapping("/api/stats")
public class StatsController {
    /** 统计服务 */
    private final StatsService statsService;
    /** 认证服务 */
    private final AuthService authService;

    /**
     * 构造函数
     * @param statsService 统计服务
     * @param authService 认证服务
     */
    public StatsController(StatsService statsService, AuthService authService) {
        this.statsService = statsService;
        this.authService = authService;
    }

    /**
     * 生成统计数据
     * @param authorization 认证令牌
     * @return 统计结果
     */
    @GetMapping
    public Map<String, Object> stats(@RequestHeader("Authorization") String authorization) {
        authService.requireUser(authorization);
        return statsService.generateStats();
    }

    /**
     * 上传统计数据到 HDFS
     * @param authorization 认证令牌
     * @return HDFS 存储路径
     */
    @PostMapping("/upload")
    public Map<String, String> upload(@RequestHeader("Authorization") String authorization) {
        authService.requireAdmin(authorization);
        String path = statsService.uploadStatsToHdfs();
        return Map.of("hdfsPath", path);
    }
}
