package com.example.vehiclemanagement.repository;

import com.example.vehiclemanagement.model.AuditRecord;

import java.io.IOException;
import java.util.List;

/**
 * 审计记录仓库接口
 * 定义审计记录的存储和查询方法
 */
public interface AuditRepository {
    /**
     * 保存审计记录
     * @param record 审计记录
     * @throws IOException 存储异常
     */
    void save(AuditRecord record) throws IOException;

    /**
     * 根据车辆ID查询审计记录
     * @param vehicleId 车辆ID
     * @return 审计记录列表
     * @throws IOException 查询异常
     */
    List<AuditRecord> findByVehicleId(String vehicleId) throws IOException;
}
