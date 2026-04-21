package com.example.vehiclemanagement.repository;

import com.example.vehiclemanagement.model.Vehicle;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * 车辆仓库接口
 * 定义车辆的存储、查询和删除方法
 */
public interface VehicleRepository {
    /**
     * 保存车辆信息
     * @param vehicle 车辆信息
     * @throws IOException 存储异常
     */
    void save(Vehicle vehicle) throws IOException;

    /**
     * 根据车辆ID查询车辆
     * @param vehicleId 车辆ID
     * @return 车辆信息，不存在则返回 Optional.empty()
     * @throws IOException 查询异常
     */
    Optional<Vehicle> findById(String vehicleId) throws IOException;

    /**
     * 查询所有车辆
     * @return 车辆列表
     * @throws IOException 查询异常
     */
    List<Vehicle> findAll() throws IOException;

    /**
     * 根据品牌查询车辆
     * @param brand 品牌
     * @return 车辆列表
     * @throws IOException 查询异常
     */
    List<Vehicle> findByBrand(String brand) throws IOException;

    /**
     * 根据状态查询车辆
     * @param status 状态
     * @return 车辆列表
     * @throws IOException 查询异常
     */
    List<Vehicle> findByStatus(String status) throws IOException;

    /**
     * 根据车辆ID删除车辆
     * @param vehicleId 车辆ID
     * @throws IOException 删除异常
     */
    void deleteById(String vehicleId) throws IOException;
}
