package com.example.vehiclemanagement.controller;

import com.example.vehiclemanagement.dto.ApiResponse;
import com.example.vehiclemanagement.dto.PageResponse;
import com.example.vehiclemanagement.dto.VehicleQueryRequest;
import com.example.vehiclemanagement.dto.VehicleRequest;
import com.example.vehiclemanagement.model.AuditRecord;
import com.example.vehiclemanagement.model.Vehicle;
import com.example.vehiclemanagement.security.UserSession;
import com.example.vehiclemanagement.service.AuthService;
import com.example.vehiclemanagement.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 车辆控制器，负责车辆增删改查和审计日志查询接口。
 */
@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {
    /** 车辆服务 */
    private final VehicleService vehicleService;
    /** 认证服务 */
    private final AuthService authService;

    /**
     * 构造函数
     * @param vehicleService 车辆服务
     * @param authService 认证服务
     */
    public VehicleController(VehicleService vehicleService, AuthService authService) {
        this.vehicleService = vehicleService;
        this.authService = authService;
    }

    /**
     * 添加车辆
     * @param authorization 认证令牌
     * @param request 车辆信息
     * @return 添加的车辆
     */
    @PostMapping
    public ApiResponse<Vehicle> add(@RequestHeader("Authorization") String authorization,
                                    @Valid @RequestBody VehicleRequest request) {
        UserSession session = authService.requireUser(authorization);
        return ApiResponse.success(vehicleService.addVehicle(request, session.getUsername()), "新增车辆成功");
    }

    /**
     * 更新车辆信息
     * @param authorization 认证令牌
     * @param vehicleId 车辆ID
     * @param request 车辆信息
     * @return 更新后的车辆
     */
    @PutMapping("/{vehicleId}")
    public ApiResponse<Vehicle> update(@RequestHeader("Authorization") String authorization,
                                       @PathVariable String vehicleId,
                                       @Valid @RequestBody VehicleRequest request) {
        UserSession session = authService.requireUser(authorization);
        return ApiResponse.success(vehicleService.updateVehicle(vehicleId, request, session.getUsername()), "修改车辆成功");
    }

    /**
     * 删除车辆
     * @param authorization 认证令牌
     * @param vehicleId 车辆ID
     * @return 删除结果
     */
    @DeleteMapping("/{vehicleId}")
    public ApiResponse<Void> delete(@RequestHeader("Authorization") String authorization,
                                    @PathVariable String vehicleId) {
        UserSession session = authService.requireAdmin(authorization);
        vehicleService.deleteVehicle(vehicleId, session.getUsername());
        return ApiResponse.success(null, "删除成功");
    }

    /**
     * 获取车辆详情
     * @param authorization 认证令牌
     * @param vehicleId 车辆ID
     * @return 车辆信息
     */
    @GetMapping("/{vehicleId}")
    public ApiResponse<Vehicle> get(@RequestHeader("Authorization") String authorization,
                                    @PathVariable String vehicleId) {
        authService.requireUser(authorization);
        return ApiResponse.success(vehicleService.getById(vehicleId));
    }

    /**
     * 列出车辆
     * @param authorization 认证令牌
     * @param brand 品牌（可选）
     * @param status 状态（可选）
     * @return 车辆列表
     */
    @GetMapping
    public ApiResponse<PageResponse<Vehicle>> list(@RequestHeader("Authorization") String authorization,
                                                   @RequestParam(required = false) String keyword,
                                                   @RequestParam(required = false) String brand,
                                                   @RequestParam(required = false) String status,
                                                   @RequestParam(required = false) String ownerName,
                                                   @RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   @RequestParam(defaultValue = "updatedAt") String sortBy,
                                                   @RequestParam(defaultValue = "desc") String sortDir) {
        authService.requireUser(authorization);
        VehicleQueryRequest request = new VehicleQueryRequest();
        request.setKeyword(keyword);
        request.setBrand(brand);
        request.setStatus(status);
        request.setOwnerName(ownerName);
        request.setPage(page);
        request.setSize(size);
        request.setSortBy(sortBy);
        request.setSortDir(sortDir);
        return ApiResponse.success(vehicleService.list(request));
    }

    /**
     * 查询车辆审计日志
     * @param authorization 认证令牌
     * @param vehicleId 车辆ID
     * @return 审计日志列表
     */
    @GetMapping("/{vehicleId}/audit")
    public ApiResponse<List<AuditRecord>> audit(@RequestHeader("Authorization") String authorization,
                                                @PathVariable String vehicleId) {
        authService.requireUser(authorization);
        return ApiResponse.success(vehicleService.listAudit(vehicleId));
    }
}
