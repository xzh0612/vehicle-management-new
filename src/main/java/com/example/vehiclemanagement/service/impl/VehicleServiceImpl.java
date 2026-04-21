package com.example.vehiclemanagement.service.impl;

import com.example.vehiclemanagement.dto.VehicleRequest;
import com.example.vehiclemanagement.exception.NotFoundException;
import com.example.vehiclemanagement.model.AuditRecord;
import com.example.vehiclemanagement.model.Vehicle;
import com.example.vehiclemanagement.repository.AuditRepository;
import com.example.vehiclemanagement.repository.VehicleRepository;
import com.example.vehiclemanagement.service.VehicleService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * 车辆服务实现类，负责车辆增删改查和审计日志写入。
 */
@Service
public class VehicleServiceImpl implements VehicleService {
    /** 车辆仓库 */
    private final VehicleRepository vehicleRepository;
    /** 审计仓库 */
    private final AuditRepository auditRepository;

    /**
     * 构造函数
     * @param vehicleRepository 车辆仓库
     * @param auditRepository 审计仓库
     */
    public VehicleServiceImpl(VehicleRepository vehicleRepository, AuditRepository auditRepository) {
        this.vehicleRepository = vehicleRepository;
        this.auditRepository = auditRepository;
    }

    /**
     * 添加车辆
     * @param request 车辆信息
     * @param operator 操作人
     * @return 添加的车辆
     */
    @Override
    public Vehicle addVehicle(VehicleRequest request, String operator) {
        Vehicle vehicle = new Vehicle();
        long now = System.currentTimeMillis();
        vehicle.setVehicleId(UUID.randomUUID().toString().replace("-", ""));
        vehicle.setPlateNumber(request.getPlateNumber());
        vehicle.setBrand(request.getBrand());
        vehicle.setModel(request.getModel());
        vehicle.setOwnerName(request.getOwnerName());
        vehicle.setPhone(request.getPhone());
        vehicle.setStatus(request.getStatus());
        vehicle.setCreatedAt(now);
        vehicle.setUpdatedAt(now);

        try {
            vehicleRepository.save(vehicle);
            saveAudit(vehicle.getVehicleId(), "ADD", operator);
            return vehicle;
        } catch (IOException e) {
            throw new IllegalStateException("新增车辆失败: " + e.getMessage(), e);
        }
    }

    /**
     * 更新车辆信息
     * @param vehicleId 车辆ID
     * @param request 车辆信息
     * @param operator 操作人
     * @return 更新后的车辆
     */
    @Override
    public Vehicle updateVehicle(String vehicleId, VehicleRequest request, String operator) {
        try {
            Vehicle existing = vehicleRepository.findById(vehicleId)
                    .orElseThrow(() -> new NotFoundException("车辆不存在"));
            existing.setPlateNumber(request.getPlateNumber());
            existing.setBrand(request.getBrand());
            existing.setModel(request.getModel());
            existing.setOwnerName(request.getOwnerName());
            existing.setPhone(request.getPhone());
            existing.setStatus(request.getStatus());
            existing.setUpdatedAt(System.currentTimeMillis());
            vehicleRepository.save(existing);
            saveAudit(vehicleId, "UPDATE", operator);
            return existing;
        } catch (IOException e) {
            throw new IllegalStateException("更新车辆失败: " + e.getMessage(), e);
        }
    }

    /**
     * 删除车辆
     * @param vehicleId 车辆ID
     * @param operator 操作人
     */
    @Override
    public void deleteVehicle(String vehicleId, String operator) {
        try {
            if (vehicleRepository.findById(vehicleId).isEmpty()) {
                throw new NotFoundException("车辆不存在");
            }
            vehicleRepository.deleteById(vehicleId);
            saveAudit(vehicleId, "DELETE", operator);
        } catch (IOException e) {
            throw new IllegalStateException("删除车辆失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据车辆ID获取车辆
     * @param vehicleId 车辆ID
     * @return 车辆信息
     */
    @Override
    public Vehicle getById(String vehicleId) {
        try {
            return vehicleRepository.findById(vehicleId)
                    .orElseThrow(() -> new NotFoundException("车辆不存在"));
        } catch (IOException e) {
            throw new IllegalStateException("查询车辆失败: " + e.getMessage(), e);
        }
    }

    /**
     * 列出车辆
     * @param brand 品牌（可选）
     * @param status 状态（可选）
     * @return 车辆列表
     */
    @Override
    public List<Vehicle> list(String brand, String status) {
        try {
            if (brand != null && !brand.isBlank()) {
                return vehicleRepository.findByBrand(brand);
            }
            if (status != null && !status.isBlank()) {
                return vehicleRepository.findByStatus(status);
            }
            return vehicleRepository.findAll();
        } catch (IOException e) {
            throw new IllegalStateException("列表查询失败: " + e.getMessage(), e);
        }
    }

    /**
     * 查询车辆审计日志
     * @param vehicleId 车辆ID
     * @return 审计日志列表
     */
    @Override
    public List<AuditRecord> listAudit(String vehicleId) {
        try {
            return auditRepository.findByVehicleId(vehicleId);
        } catch (IOException e) {
            throw new IllegalStateException("查询审计日志失败: " + e.getMessage(), e);
        }
    }

    /**
     * 保存审计记录
     * @param vehicleId 车辆ID
     * @param operation 操作类型
     * @param operator 操作人
     * @throws IOException 异常
     */
    private void saveAudit(String vehicleId, String operation, String operator) throws IOException {
        AuditRecord record = new AuditRecord();
        record.setRecordId(UUID.randomUUID().toString().replace("-", ""));
        record.setVehicleId(vehicleId);
        record.setOperation(operation);
        record.setOperator(operator);
        record.setTimestamp(System.currentTimeMillis());
        auditRepository.save(record);
    }
}
