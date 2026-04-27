package com.example.vehiclemanagement.service.impl;

import com.example.vehiclemanagement.config.HdfsProperties;
import com.example.vehiclemanagement.model.Vehicle;
import com.example.vehiclemanagement.repository.VehicleRepository;
import com.example.vehiclemanagement.service.StatsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 统计服务实现类，负责汇总车辆数据并将结果写入 HDFS。
 */
@Service
public class StatsServiceImpl implements StatsService {
    /** 车辆仓库 */
    private final VehicleRepository vehicleRepository;
    /** HDFS 配置属性 */
    private final HdfsProperties hdfsProperties;
    /** JSON 序列化工具 */
    private final ObjectMapper objectMapper;

    /**
     * 构造函数
     * @param vehicleRepository 车辆仓库
     * @param hdfsProperties HDFS 配置属性
     */
    public StatsServiceImpl(VehicleRepository vehicleRepository,
                            HdfsProperties hdfsProperties,
                            ObjectMapper objectMapper) {
        this.vehicleRepository = vehicleRepository;
        this.hdfsProperties = hdfsProperties;
        this.objectMapper = objectMapper;
    }

    /**
     * 生成统计数据
     * @return 统计结果
     */
    @Override
    public Map<String, Object> generateStats() {
        try {
            List<Vehicle> vehicles = vehicleRepository.findAll();
            Map<String, Object> stats = new LinkedHashMap<>();
            stats.put("generatedAt", Instant.now().toString());
            stats.put("total", vehicles.size());
            stats.put("byBrand", groupCount(vehicles, Vehicle::getBrand));
            stats.put("byStatus", groupCount(vehicles, Vehicle::getStatus));
            stats.put("byCreator", groupCount(vehicles, Vehicle::getCreatedBy));
            stats.put("inspectionReminders", buildExpiryStats(vehicles, true));
            stats.put("insuranceReminders", buildExpiryStats(vehicles, false));
            stats.put("recentVehicles", vehicles.stream()
                    .sorted(Comparator.comparingLong(Vehicle::getCreatedAt).reversed())
                    .limit(5)
                    .map(this::toRecentVehicleSummary)
                    .collect(Collectors.toList()));
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
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(stats);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("统计序列化失败: " + e.getMessage(), e);
        }
    }

    private Map<String, Long> groupCount(List<Vehicle> vehicles, java.util.function.Function<Vehicle, String> keyMapper) {
        return vehicles.stream()
                .collect(Collectors.groupingBy(
                        vehicle -> normalizeLabel(keyMapper.apply(vehicle)),
                        LinkedHashMap::new,
                        Collectors.counting()
                ));
    }

    private Map<String, Object> buildExpiryStats(List<Vehicle> vehicles, boolean inspection) {
        LocalDate today = LocalDate.now();
        long overdue = 0;
        long expiringSoon = 0;

        for (Vehicle vehicle : vehicles) {
            String value = inspection ? vehicle.getAnnualInspectionDate() : vehicle.getInsuranceExpireDate();
            if (value == null || value.isBlank()) {
                continue;
            }
            LocalDate date;
            try {
                date = LocalDate.parse(value);
            } catch (Exception ex) {
                continue;
            }
            long days = ChronoUnit.DAYS.between(today, date);
            if (days < 0) {
                overdue++;
            } else if (days <= 30) {
                expiringSoon++;
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("overdue", overdue);
        result.put("expiringWithin30Days", expiringSoon);
        return result;
    }

    private String normalizeLabel(String value) {
        return value == null || value.isBlank() ? "UNKNOWN" : value;
    }

    private Map<String, Object> toRecentVehicleSummary(Vehicle vehicle) {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("vehicleId", normalizeLabel(vehicle.getVehicleId()));
        summary.put("plateNumber", normalizeLabel(vehicle.getPlateNumber()));
        summary.put("brand", normalizeLabel(vehicle.getBrand()));
        summary.put("ownerName", normalizeLabel(vehicle.getOwnerName()));
        summary.put("status", normalizeLabel(vehicle.getStatus()));
        return summary;
    }
}
