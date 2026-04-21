package com.example.vehiclemanagement.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * HBase 配置属性类
 * 从 application.yml 中读取 HBase 相关配置
 */
@Component
@ConfigurationProperties(prefix = "hbase")
public class HBaseProperties {
    /** ZooKeeper 集群地址 */
    private String zookeeperQuorum;
    /** ZooKeeper 客户端端口 */
    private String zookeeperClientPort;
    /** 用户表名 */
    private String tableUsers;
    /** 车辆表名 */
    private String tableVehicles;
    /** 审计表名 */
    private String tableAudit;

    /**
     * 获取 ZooKeeper 集群地址
     * @return ZooKeeper 集群地址
     */
    public String getZookeeperQuorum() {
        return zookeeperQuorum;
    }

    /**
     * 设置 ZooKeeper 集群地址
     * @param zookeeperQuorum ZooKeeper 集群地址
     */
    public void setZookeeperQuorum(String zookeeperQuorum) {
        this.zookeeperQuorum = zookeeperQuorum;
    }

    /**
     * 获取 ZooKeeper 客户端端口
     * @return ZooKeeper 客户端端口
     */
    public String getZookeeperClientPort() {
        return zookeeperClientPort;
    }

    /**
     * 设置 ZooKeeper 客户端端口
     * @param zookeeperClientPort ZooKeeper 客户端端口
     */
    public void setZookeeperClientPort(String zookeeperClientPort) {
        this.zookeeperClientPort = zookeeperClientPort;
    }

    /**
     * 获取用户表名
     * @return 用户表名
     */
    public String getTableUsers() {
        return tableUsers;
    }

    /**
     * 设置用户表名
     * @param tableUsers 用户表名
     */
    public void setTableUsers(String tableUsers) {
        this.tableUsers = tableUsers;
    }

    /**
     * 获取车辆表名
     * @return 车辆表名
     */
    public String getTableVehicles() {
        return tableVehicles;
    }

    /**
     * 设置车辆表名
     * @param tableVehicles 车辆表名
     */
    public void setTableVehicles(String tableVehicles) {
        this.tableVehicles = tableVehicles;
    }

    /**
     * 获取审计表名
     * @return 审计表名
     */
    public String getTableAudit() {
        return tableAudit;
    }

    /**
     * 设置审计表名
     * @param tableAudit 审计表名
     */
    public void setTableAudit(String tableAudit) {
        this.tableAudit = tableAudit;
    }
}
