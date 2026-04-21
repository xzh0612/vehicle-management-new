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
