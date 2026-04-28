package com.example.vehiclemanagement.util;

/**
 * 系统用户角色。
 */
public enum UserRole {
    ADMIN,
    USER;

    /**
     * 判断当前角色是否为管理员。
     * @return true 表示管理员
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }
}
