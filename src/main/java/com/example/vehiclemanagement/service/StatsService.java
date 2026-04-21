package com.example.vehiclemanagement.service;

import java.util.Map;

/**
 * 统计服务接口，负责生成业务统计并上传统计结果到 HDFS。
 */
public interface StatsService {
    /**
     * 生成统计数据
     * @return 统计结果
     */
    Map<String, Object> generateStats();

    /**
     * 上传统计数据到 HDFS
     * @return HDFS 存储路径
     */
    String uploadStatsToHdfs();
}
