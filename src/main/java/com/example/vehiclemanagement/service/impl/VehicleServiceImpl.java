package com.example.vehiclemanagement.service.impl;

import com.example.vehiclemanagement.dto.PageResponse;
import com.example.vehiclemanagement.dto.VehicleQueryRequest;
import com.example.vehiclemanagement.dto.VehicleRequest;
import com.example.vehiclemanagement.exception.BadRequestException;
import com.example.vehiclemanagement.exception.NotFoundException;
import com.example.vehiclemanagement.model.AuditRecord;
import com.example.vehiclemanagement.model.Vehicle;
import com.example.vehiclemanagement.repository.AuditRepository;
import com.example.vehiclemanagement.repository.VehicleRepository;
import com.example.vehiclemanagement.service.VehicleService;
import com.example.vehiclemanagement.util.VehicleStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

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
        validateRequest(request);
        Vehicle vehicle = new Vehicle();
        long now = System.currentTimeMillis();
        vehicle.setVehicleId(UUID.randomUUID().toString().replace("-", ""));
        vehicle.setPlateNumber(request.getPlateNumber());
        vehicle.setVin(request.getVin());
        vehicle.setEngineNumber(request.getEngineNumber());
        vehicle.setBrand(request.getBrand());
        vehicle.setModel(request.getModel());
        vehicle.setOwnerName(request.getOwnerName());
        vehicle.setPhone(request.getPhone());
        vehicle.setStatus(VehicleStatus.from(request.getStatus()).name());
        vehicle.setRegisterDate(request.getRegisterDate());
        vehicle.setAnnualInspectionDate(request.getAnnualInspectionDate());
        vehicle.setInsuranceExpireDate(request.getInsuranceExpireDate());
        vehicle.setMileage(request.getMileage());
        vehicle.setRemark(trimToEmpty(request.getRemark()));
        vehicle.setCreatedBy(operator);
        vehicle.setCreatedAt(now);
        vehicle.setUpdatedAt(now);

        try {
            ensureUnique(vehicle.getPlateNumber(), vehicle.getVin(), null);
            vehicleRepository.save(vehicle);
            saveAudit(vehicle.getVehicleId(), "CREATE", operator,
                    "新建车辆 " + vehicle.getPlateNumber() + "，状态为 " + vehicle.getStatus());
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
        validateRequest(request);
        try {
            Vehicle existing = vehicleRepository.findById(vehicleId)
                    .orElseThrow(() -> new NotFoundException("车辆不存在"));
            ensureUnique(request.getPlateNumber(), request.getVin(), vehicleId);
            String beforeStatus = existing.getStatus();
            existing.setPlateNumber(request.getPlateNumber());
            existing.setVin(request.getVin());
            existing.setEngineNumber(request.getEngineNumber());
            existing.setBrand(request.getBrand());
            existing.setModel(request.getModel());
            existing.setOwnerName(request.getOwnerName());
            existing.setPhone(request.getPhone());
            existing.setStatus(VehicleStatus.from(request.getStatus()).name());
            existing.setRegisterDate(request.getRegisterDate());
            existing.setAnnualInspectionDate(request.getAnnualInspectionDate());
            existing.setInsuranceExpireDate(request.getInsuranceExpireDate());
            existing.setMileage(request.getMileage());
            existing.setRemark(trimToEmpty(request.getRemark()));
            existing.setUpdatedAt(System.currentTimeMillis());
            vehicleRepository.save(existing);
            saveAudit(vehicleId, "UPDATE", operator, buildUpdateDetail(existing, beforeStatus));
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
            saveAudit(vehicleId, "DELETE", operator, "删除车辆记录");
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
    public PageResponse<Vehicle> list(VehicleQueryRequest request) {
        try {
            List<Vehicle> filtered = vehicleRepository.findAll().stream()
                    .filter(vehicle -> matchesKeyword(vehicle, request.getKeyword()))
                    .filter(vehicle -> matchesIgnoreCase(vehicle.getBrand(), request.getBrand()))
                    .filter(vehicle -> matchesIgnoreCase(vehicle.getStatus(), request.getStatus()))
                    .filter(vehicle -> matchesIgnoreCase(vehicle.getOwnerName(), request.getOwnerName()))
                    .sorted(buildComparator(request.getSortBy(), request.getSortDir()))
                    .collect(Collectors.toList());

            int size = normalizeSize(request.getSize());
            int page = normalizePage(request.getPage());
            int fromIndex = Math.min((page - 1) * size, filtered.size());
            int toIndex = Math.min(fromIndex + size, filtered.size());
            List<Vehicle> items = filtered.subList(fromIndex, toIndex);
            int totalPages = filtered.isEmpty() ? 0 : (int) Math.ceil((double) filtered.size() / size);
            return new PageResponse<>(items, filtered.size(), page, size, totalPages);
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
    private void saveAudit(String vehicleId, String operation, String operator, String detail) throws IOException {
        AuditRecord record = new AuditRecord();
        record.setRecordId(UUID.randomUUID().toString().replace("-", ""));
        record.setVehicleId(vehicleId);
        record.setOperation(operation);
        record.setOperator(operator);
        record.setDetail(detail);
        record.setTimestamp(System.currentTimeMillis());
        auditRepository.save(record);
    }

    private void validateRequest(VehicleRequest request) {
        parseDate(request.getRegisterDate(), "登记日期");
        parseDate(request.getAnnualInspectionDate(), "年检到期日期");
        parseDate(request.getInsuranceExpireDate(), "保险到期日期");
        VehicleStatus.from(request.getStatus());
    }

    private void ensureUnique(String plateNumber, String vin, String currentVehicleId) throws IOException {
        vehicleRepository.findByPlateNumber(plateNumber)
                .filter(vehicle -> !Objects.equals(vehicle.getVehicleId(), currentVehicleId))
                .ifPresent(vehicle -> {
                    throw new BadRequestException("车牌号已存在");
                });
        vehicleRepository.findByVin(vin)
                .filter(vehicle -> !Objects.equals(vehicle.getVehicleId(), currentVehicleId))
                .ifPresent(vehicle -> {
                    throw new BadRequestException("车架号已存在");
                });
    }

    private String buildUpdateDetail(Vehicle vehicle, String beforeStatus) {
        if (!Objects.equals(beforeStatus, vehicle.getStatus())) {
            return "更新车辆信息，状态从 " + beforeStatus + " 变更为 " + vehicle.getStatus();
        }
        return "更新车辆信息";
    }

    private boolean matchesKeyword(Vehicle vehicle, String keyword) {
        String normalized = trimToEmpty(keyword).toLowerCase(Locale.ROOT);
        if (normalized.isEmpty()) {
            return true;
        }
        return contains(vehicle.getPlateNumber(), normalized)
                || contains(vehicle.getVin(), normalized)
                || contains(vehicle.getBrand(), normalized)
                || contains(vehicle.getModel(), normalized)
                || contains(vehicle.getOwnerName(), normalized)
                || contains(vehicle.getPhone(), normalized);
    }

    private boolean contains(String source, String keyword) {
        return source != null && source.toLowerCase(Locale.ROOT).contains(keyword);
    }

    private boolean matchesIgnoreCase(String source, String target) {
        String normalized = trimToEmpty(target);
        return normalized.isEmpty() || source != null && source.equalsIgnoreCase(normalized);
    }

    private Comparator<Vehicle> buildComparator(String sortBy, String sortDir) {
        Comparator<Vehicle> comparator = switch (trimToEmpty(sortBy)) {
            case "plateNumber" -> Comparator.comparing(vehicle -> safeText(vehicle.getPlateNumber()));
            case "brand" -> Comparator.comparing(vehicle -> safeText(vehicle.getBrand()));
            case "ownerName" -> Comparator.comparing(vehicle -> safeText(vehicle.getOwnerName()));
            case "mileage" -> Comparator.comparingLong(Vehicle::getMileage);
            case "createdAt" -> Comparator.comparingLong(Vehicle::getCreatedAt);
            default -> Comparator.comparingLong(Vehicle::getUpdatedAt);
        };
        return "asc".equalsIgnoreCase(trimToEmpty(sortDir)) ? comparator : comparator.reversed();
    }

    private int normalizePage(int page) {
        return Math.max(page, 1);
    }

    private int normalizeSize(int size) {
        return Math.min(Math.max(size, 1), 100);
    }

    private void parseDate(String value, String fieldName) {
        try {
            LocalDate.parse(value);
        } catch (DateTimeParseException ex) {
            throw new BadRequestException(fieldName + "格式必须为 yyyy-MM-dd");
        }
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private String safeText(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
    }
}
