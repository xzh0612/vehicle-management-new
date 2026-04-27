package com.example.vehiclemanagement.model;

/**
 * 车辆模型类
 * 用于表示车辆信息
 */
public class Vehicle {
    /** 车辆ID */
    private String vehicleId;
    /** 车牌号 */
    private String plateNumber;
    /** 车架号 */
    private String vin;
    /** 发动机号 */
    private String engineNumber;
    /** 品牌 */
    private String brand;
    /** 型号 */
    private String model;
    /** 车主姓名 */
    private String ownerName;
    /** 联系电话 */
    private String phone;
    /** 状态 */
    private String status;
    /** 首次登记日期 */
    private String registerDate;
    /** 年检到期日期 */
    private String annualInspectionDate;
    /** 保险到期日期 */
    private String insuranceExpireDate;
    /** 里程数 */
    private long mileage;
    /** 备注 */
    private String remark;
    /** 创建人 */
    private String createdBy;
    /** 创建时间戳 */
    private long createdAt;
    /** 更新时间戳 */
    private long updatedAt;

    /**
     * 获取车辆ID
     * @return 车辆ID
     */
    public String getVehicleId() {
        return vehicleId;
    }

    /**
     * 设置车辆ID
     * @param vehicleId 车辆ID
     */
    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

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

    public long getMileage() {
        return mileage;
    }

    public void setMileage(long mileage) {
        this.mileage = mileage;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * 获取创建时间戳
     * @return 创建时间戳
     */
    public long getCreatedAt() {
        return createdAt;
    }

    /**
     * 设置创建时间戳
     * @param createdAt 创建时间戳
     */
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 获取更新时间戳
     * @return 更新时间戳
     */
    public long getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 设置更新时间戳
     * @param updatedAt 更新时间戳
     */
    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
