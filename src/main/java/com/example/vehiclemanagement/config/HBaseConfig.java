package com.example.vehiclemanagement.config;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.io.IOException;

/**
 * HBase 配置类
 * 负责初始化 HBase 连接和表结构
 */
@org.springframework.context.annotation.Configuration
public class HBaseConfig {

    /**
     * 创建 HBase 配置对象
     * @param properties HBase 配置属性
     * @return HBase 配置对象
     */
    @Bean
    @Primary
    public Configuration hbaseConfiguration(HBaseProperties properties) {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", properties.getZookeeperQuorum());
        conf.set("hbase.zookeeper.property.clientPort", properties.getZookeeperClientPort());
        return conf;
    }

    /**
     * 创建 HBase 连接
     * @param configuration HBase 配置对象
     * @return HBase 连接
     * @throws IOException 连接异常
     */
    @Bean(destroyMethod = "close")
    public Connection hbaseConnection(Configuration configuration) throws IOException {
        return ConnectionFactory.createConnection(configuration);
    }

    /**
     * 创建表初始化器
     * @param connection HBase 连接
     * @param properties HBase 配置属性
     * @return 表初始化器
     */
    @Bean
    public TableInitializer tableInitializer(Connection connection, HBaseProperties properties) {
        return new TableInitializer(connection, properties);
    }

    /**
     * 表初始化器内部类
     * 负责创建必要的 HBase 表
     */
    public static class TableInitializer {
        /** HBase 连接 */
        private final Connection connection;
        /** HBase 配置属性 */
        private final HBaseProperties properties;

        /**
         * 构造函数
         * @param connection HBase 连接
         * @param properties HBase 配置属性
         */
        public TableInitializer(Connection connection, HBaseProperties properties) {
            this.connection = connection;
            this.properties = properties;
            initTables();
        }

        /**
         * 初始化表结构
         */
        private void initTables() {
            try (Admin admin = connection.getAdmin()) {
                createIfAbsent(admin, properties.getTableUsers(), "info");
                createIfAbsent(admin, properties.getTableVehicles(), "info");
                createIfAbsent(admin, properties.getTableAudit(), "info");
            } catch (IOException e) {
                throw new IllegalStateException("HBase建表失败: " + e.getMessage(), e);
            }
        }

        /**
         * 检查表是否存在，不存在则创建
         * @param admin HBase 管理员
         * @param table 表名
         * @param family 列族名
         * @throws IOException 操作异常
         */
        private void createIfAbsent(Admin admin, String table, String family) throws IOException {
            TableName tableName = TableName.valueOf(table);
            if (admin.tableExists(tableName)) {
                return;
            }
            TableDescriptor descriptor = TableDescriptorBuilder.newBuilder(tableName)
                    .setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(family.getBytes()).build())
                    .build();
            admin.createTable(descriptor);
        }
    }
}
