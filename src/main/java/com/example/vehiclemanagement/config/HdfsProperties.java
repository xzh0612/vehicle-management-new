package com.example.vehiclemanagement.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * HDFS 配置属性类
 * 从 application.yml 中读取 HDFS 相关配置
 */
@Component
@ConfigurationProperties(prefix = "hdfs")
public class HdfsProperties {
    /** HDFS 服务地址 */
    private String uri;
    /** HDFS 用户名 */
    private String user;
    /** 统计数据存储目录 */
    private String statsDir;

    /**
     * 获取 HDFS 服务地址
     * @return HDFS 服务地址
     */
    public String getUri() {
        return uri;
    }

    /**
     * 设置 HDFS 服务地址
     * @param uri HDFS 服务地址
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * 获取 HDFS 用户名
     * @return HDFS 用户名
     */
    public String getUser() {
        return user;
    }

    /**
     * 设置 HDFS 用户名
     * @param user HDFS 用户名
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * 获取统计数据存储目录
     * @return 统计数据存储目录
     */
    public String getStatsDir() {
        return statsDir;
    }

    /**
     * 设置统计数据存储目录
     * @param statsDir 统计数据存储目录
     */
    public void setStatsDir(String statsDir) {
        this.statsDir = statsDir;
    }
}
