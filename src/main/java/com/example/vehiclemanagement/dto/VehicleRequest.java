package com.example.vehiclemanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * 车辆请求类
 * 用于接收车辆信息
 */
public class VehicleRequest {
    /** 车牌号 */
    @NotBlank
    private String plateNumber;
    /** 车架号 */
    @NotBlank
    @Size(min = 6, max = 32)
    private String vin;
    /** 发动机号 */
    @NotBlank
    private String engineNumber;
    /** 品牌 */
    @NotBlank
    private String brand;
    /** 型号 */
    @NotBlank
    private String model;
    /** 车主姓名 */
    @NotBlank
    private String ownerName;
    /** 联系电话 */
    @NotBlank
    @Pattern(regexp = "^[0-9+\\- ]{6,20}$", message = "格式不正确")
    private String phone;
    /** 状态 */
    @NotBlank
    private String status;
    /** 首次登记日期 */
    @NotBlank
    private String registerDate;
    /** 年检到期日期 */
    @NotBlank
    private String annualInspectionDate;
    /** 保险到期日期 */
    @NotBlank
    private String insuranceExpireDate;
    /** 里程数 */
    @NotNull
    @PositiveOrZero
    private Long mileage;
    /** 备注 */
    private String remark;

    /**
     * 获取车牌号
     * @return 车牌号
     */
    public String getPlateNumber() {
        return plateNumber;
    }

    /**
     * 设置车牌号
     * @param plateNumber 车牌号
     */
    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getEngineNumber() {
        return engineNumber;
    }

    public void setEngineNumber(String engineNumber) {
        this.engineNumber = engineNumber;
    }

    /**
     * 获取品牌
     * @return 品牌
     */
    public String getBrand() {
        return brand;
    }

    /**
     * 设置品牌
     * @param brand 品牌
     */
    public void setBrand(String brand) {
        this.brand = brand;
    }

    /**
     * 获取型号
     * @return 型号
     */
    public String getModel() {
        return model;
    }

    /**
     * 设置型号
     * @param model 型号
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * 获取车主姓名
     * @return 车主姓名
     */
    public String getOwnerName() {
        return ownerName;
    }

    /**
     * 设置车主姓名
     * @param ownerName 车主姓名
     */
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    /**
     * 获取联系电话
     * @return 联系电话
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 设置联系电话
     * @param phone 联系电话
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 获取状态
     * @return 状态
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置状态
     * @param status 状态
     */
    public void setStatus(String status) {
        this.status = status;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }

    public String getAnnualInspectionDate() {
        return annualInspectionDate;
    }

    public void setAnnualInspectionDate(String annualInspectionDate) {
        this.annualInspectionDate = annualInspectionDate;
    }

    public String getInsuranceExpireDate() {
        return insuranceExpireDate;
    }

    public void setInsuranceExpireDate(String insuranceExpireDate) {
        this.insuranceExpireDate = insuranceExpireDate;
    }

    public Long getMileage() {
        return mileage;
    }

    public void setMileage(Long mileage) {
        this.mileage = mileage;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
