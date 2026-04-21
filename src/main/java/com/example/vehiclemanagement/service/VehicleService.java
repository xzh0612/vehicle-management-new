package com.example.vehiclemanagement.service;

import com.example.vehiclemanagement.dto.VehicleRequest;
import com.example.vehiclemanagement.model.AuditRecord;
import com.example.vehiclemanagement.model.Vehicle;

import java.util.List;

/**
 * 车辆服务接口，负责车辆数据与审计日志的业务处理。
 */
public interface VehicleService {
    /**
     * 添加车辆
     * @param request 车辆信息
     * @param operator 操作人
     * @return 添加的车辆
     */
    Vehicle addVehicle(VehicleRequest request, String operator);

    /**
     * 更新车辆信息
     * @param vehicleId 车辆ID
     * @param request 车辆信息
     * @param operator 操作人
     * @return 更新后的车辆
     */
    Vehicle updateVehicle(String vehicleId, VehicleRequest request, String operator);

    /**
     * 删除车辆
     * @param vehicleId 车辆ID
     * @param operator 操作人
     */
    void deleteVehicle(String vehicleId, String operator);

    /**
     * 根据车辆ID获取车辆
     * @param vehicleId 车辆ID
     * @return 车辆信息
     */
    Vehicle getById(String vehicleId);

    /**
     * 列出车辆
     * @param brand 品牌（可选）
     * @param status 状态（可选）
     * @return 车辆列表
     */
    List<Vehicle> list(String brand, String status);

    /**
     * 查询车辆审计日志
     * @param vehicleId 车辆ID
     * @return 审计日志列表
     */
    List<AuditRecord> listAudit(String vehicleId);
}
