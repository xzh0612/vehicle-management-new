package com.example.vehiclemanagement.util;

import java.util.Arrays;

/**
 * 车辆状态枚举。
 */
public enum VehicleStatus {
    ACTIVE,
    MAINTENANCE,
    SCRAPPED,
    TRANSFERRED;

    /**
     * 将字符串解析为状态枚举。
     * @param value 输入值
     * @return 状态枚举
     */
    public static VehicleStatus from(String value) {
        return Arrays.stream(values())
                .filter(status -> status.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的车辆状态: " + value));
    }
}
