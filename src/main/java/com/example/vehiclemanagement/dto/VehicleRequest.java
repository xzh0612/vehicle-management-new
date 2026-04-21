package com.example.vehiclemanagement.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 车辆请求类
 * 用于接收车辆信息
 */
public class VehicleRequest {
    /** 车牌号 */
    @NotBlank
    private String plateNumber;
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
    private String phone;
    /** 状态 */
    @NotBlank
    private String status;

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
}
