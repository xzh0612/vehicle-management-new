package com.example.vehiclemanagement.model;

/**
 * 审计记录类
 * 用于记录车辆操作的审计信息
 */
public class AuditRecord {
    /** 记录ID */
    private String recordId;
    /** 车辆ID */
    private String vehicleId;
    /** 操作类型 */
    private String operation;
    /** 操作人 */
    private String operator;
    /** 操作时间戳 */
    private long timestamp;

    /**
     * 获取记录ID
     * @return 记录ID
     */
    public String getRecordId() {
        return recordId;
    }

    /**
     * 设置记录ID
     * @param recordId 记录ID
     */
    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

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
     * 获取操作类型
     * @return 操作类型
     */
    public String getOperation() {
        return operation;
    }

    /**
     * 设置操作类型
     * @param operation 操作类型
     */
    public void setOperation(String operation) {
        this.operation = operation;
    }

    /**
     * 获取操作人
     * @return 操作人
     */
    public String getOperator() {
        return operator;
    }

    /**
     * 设置操作人
     * @param operator 操作人
     */
    public void setOperator(String operator) {
        this.operator = operator;
    }

    /**
     * 获取操作时间戳
     * @return 操作时间戳
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * 设置操作时间戳
     * @param timestamp 操作时间戳
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
