package com.example.vehiclemanagement.service.impl;

import com.example.vehiclemanagement.config.HdfsProperties;
import com.example.vehiclemanagement.model.Vehicle;
import com.example.vehiclemanagement.repository.VehicleRepository;
import com.example.vehiclemanagement.service.StatsService;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计服务实现类，负责汇总车辆数据并将结果写入 HDFS。
 */
@Service
public class StatsServiceImpl implements StatsService {
    /** 车辆仓库 */
    private final VehicleRepository vehicleRepository;
    /** HDFS 配置属性 */
    private final HdfsProperties hdfsProperties;

    /**
     * 构造函数
     * @param vehicleRepository 车辆仓库
     * @param hdfsProperties HDFS 配置属性
     */
    public StatsServiceImpl(VehicleRepository vehicleRepository, HdfsProperties hdfsProperties) {
        this.vehicleRepository = vehicleRepository;
        this.hdfsProperties = hdfsProperties;
    }

    /**
     * 生成统计数据
     * @return 统计结果
     */
    @Override
    public Map<String, Object> generateStats() {
        try {
            List<Vehicle> vehicles = vehicleRepository.findAll();
            Map<String, Long> byBrand = new HashMap<>();
            Map<String, Long> byStatus = new HashMap<>();

            for (Vehicle vehicle : vehicles) {
                byBrand.merge(vehicle.getBrand(), 1L, Long::sum);
                byStatus.merge(vehicle.getStatus(), 1L, Long::sum);
            }

            Map<String, Object> stats = new HashMap<>();
            stats.put("generatedAt", Instant.now().toString());
            stats.put("total", vehicles.size());
            stats.put("byBrand", byBrand);
            stats.put("byStatus", byStatus);
            return stats;
        } catch (IOException e) {
            throw new IllegalStateException("统计失败: " + e.getMessage(), e);
        }
    }

    /**
     * 上传统计数据到 HDFS
     * @return HDFS 存储路径
     */
    @Override
    public String uploadStatsToHdfs() {
        Map<String, Object> stats = generateStats();
        String fileName = "stats-" + System.currentTimeMillis() + ".json";
        String dstPath = hdfsProperties.getStatsDir() + "/" + fileName;

        Configuration configuration = new Configuration();
        configuration.set("fs.defaultFS", hdfsProperties.getUri());

        try (FileSystem fs = FileSystem.get(URI.create(hdfsProperties.getUri()), configuration, hdfsProperties.getUser())) {
            Path dirPath = new Path(hdfsProperties.getStatsDir());
            if (!fs.exists(dirPath)) {
                fs.mkdirs(dirPath);
            }
            Path path = new Path(dstPath);
            String json = toJson(stats);
            try (FSDataOutputStream out = fs.create(path, true)) {
                out.write(json.getBytes(StandardCharsets.UTF_8));
            }
            return dstPath;
        } catch (Exception e) {
            throw new IllegalStateException("上传统计到HDFS失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将统计数据转换为 JSON 字符串
     * @param stats 统计数据
     * @return JSON 字符串
     */
    private String toJson(Map<String, Object> stats) {
        String generatedAt = String.valueOf(stats.get("generatedAt"));
        int total = (Integer) stats.get("total");
        @SuppressWarnings("unchecked")
        Map<String, Long> byBrand = (Map<String, Long>) stats.get("byBrand");
        @SuppressWarnings("unchecked")
        Map<String, Long> byStatus = (Map<String, Long>) stats.get("byStatus");

        return "{" +
                "\"generatedAt\":\"" + generatedAt + "\"," +
                "\"total\":" + total + "," +
                "\"byBrand\":" + mapToJson(byBrand) + "," +
                "\"byStatus\":" + mapToJson(byStatus) +
                "}";
    }

    /**
     * 将 Map 转换为 JSON 字符串
     * @param map 映射
     * @return JSON 字符串
     */
    private String mapToJson(Map<String, Long> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Long> entry : map.entrySet()) {
            if (!first) {
                sb.append(',');
            }
            sb.append("\"").append(entry.getKey()).append("\":").append(entry.getValue());
            first = false;
        }
        sb.append('}');
        return sb.toString();
    }
}
