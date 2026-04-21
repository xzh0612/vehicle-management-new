package com.example.vehiclemanagement.controller;

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
import java.util.Map;

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
    public Vehicle add(@RequestHeader("Authorization") String authorization,
                       @Valid @RequestBody VehicleRequest request) {
        UserSession session = authService.requireUser(authorization);
        return vehicleService.addVehicle(request, session.getUsername());
    }

    /**
     * 更新车辆信息
     * @param authorization 认证令牌
     * @param vehicleId 车辆ID
     * @param request 车辆信息
     * @return 更新后的车辆
     */
    @PutMapping("/{vehicleId}")
    public Vehicle update(@RequestHeader("Authorization") String authorization,
                          @PathVariable String vehicleId,
                          @Valid @RequestBody VehicleRequest request) {
        UserSession session = authService.requireUser(authorization);
        return vehicleService.updateVehicle(vehicleId, request, session.getUsername());
    }

    /**
     * 删除车辆
     * @param authorization 认证令牌
     * @param vehicleId 车辆ID
     * @return 删除结果
     */
    @DeleteMapping("/{vehicleId}")
    public Map<String, Object> delete(@RequestHeader("Authorization") String authorization,
                                      @PathVariable String vehicleId) {
        UserSession session = authService.requireAdmin(authorization);
        vehicleService.deleteVehicle(vehicleId, session.getUsername());
        return Map.of("message", "删除成功");
    }

    /**
     * 获取车辆详情
     * @param authorization 认证令牌
     * @param vehicleId 车辆ID
     * @return 车辆信息
     */
    @GetMapping("/{vehicleId}")
    public Vehicle get(@RequestHeader("Authorization") String authorization,
                       @PathVariable String vehicleId) {
        authService.requireUser(authorization);
        return vehicleService.getById(vehicleId);
    }

    /**
     * 列出车辆
     * @param authorization 认证令牌
     * @param brand 品牌（可选）
     * @param status 状态（可选）
     * @return 车辆列表
     */
    @GetMapping
    public List<Vehicle> list(@RequestHeader("Authorization") String authorization,
                              @RequestParam(required = false) String brand,
                              @RequestParam(required = false) String status) {
        authService.requireUser(authorization);
        return vehicleService.list(brand, status);
    }

    /**
     * 查询车辆审计日志
     * @param authorization 认证令牌
     * @param vehicleId 车辆ID
     * @return 审计日志列表
     */
    @GetMapping("/{vehicleId}/audit")
    public List<AuditRecord> audit(@RequestHeader("Authorization") String authorization,
                                   @PathVariable String vehicleId) {
        authService.requireUser(authorization);
        return vehicleService.listAudit(vehicleId);
    }
}
