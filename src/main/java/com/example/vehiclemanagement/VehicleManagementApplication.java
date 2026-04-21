package com.example.vehiclemanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 车辆管理系统应用程序主类
 */
@SpringBootApplication
public class VehicleManagementApplication {
    /**
     * 应用程序入口方法
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(VehicleManagementApplication.class, args);
    }
}
