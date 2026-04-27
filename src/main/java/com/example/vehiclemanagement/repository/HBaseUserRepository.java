package com.example.vehiclemanagement.repository;

import com.example.vehiclemanagement.config.HBaseProperties;
import com.example.vehiclemanagement.model.User;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Optional;

/**
 * HBase 用户仓库实现
 * 使用 HBase 存储和查询用户信息
 */
@Repository
public class HBaseUserRepository implements UserRepository {
    /** 列族 */
    private static final byte[] CF = Bytes.toBytes("info");

    /** HBase 连接 */
    private final Connection connection;
    /** HBase 配置属性 */
    private final HBaseProperties properties;

    /**
     * 构造函数
     * @param connection HBase 连接
     * @param properties HBase 配置属性
     */
    public HBaseUserRepository(Connection connection, HBaseProperties properties) {
        this.connection = connection;
        this.properties = properties;
    }

    /**
     * 保存用户信息
     * @param user 用户信息
     * @throws IOException 存储异常
     */
    @Override
    public void save(User user) throws IOException {
        try (Table table = connection.getTable(TableName.valueOf(properties.getTableUsers()))) {
            Put put = new Put(Bytes.toBytes(user.getUsername()));
            put.addColumn(CF, Bytes.toBytes("username"), Bytes.toBytes(user.getUsername()));
            put.addColumn(CF, Bytes.toBytes("passwordHash"), Bytes.toBytes(user.getPasswordHash()));
            put.addColumn(CF, Bytes.toBytes("role"), Bytes.toBytes(user.getRole()));
            table.put(put);
        }
    }

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息，不存在则返回 Optional.empty()
     * @throws IOException 查询异常
     */
    @Override
    public Optional<User> findByUsername(String username) throws IOException {
        try (Table table = connection.getTable(TableName.valueOf(properties.getTableUsers()))) {
            Get get = new Get(Bytes.toBytes(username));
            Result result = table.get(get);
            if (result.isEmpty()) {
                return Optional.empty();
            }
            User user = new User();
            String storedUsername = Bytes.toString(result.getValue(CF, Bytes.toBytes("username")));
            user.setUsername(storedUsername == null || storedUsername.isBlank() ? username : storedUsername);
            byte[] passwordHash = result.getValue(CF, Bytes.toBytes("passwordHash"));
            if (passwordHash == null) {
                passwordHash = result.getValue(CF, Bytes.toBytes("password"));
            }
            user.setPasswordHash(Bytes.toString(passwordHash));
            String role = Bytes.toString(result.getValue(CF, Bytes.toBytes("role")));
            user.setRole(role == null || role.isBlank() ? "USER" : role);
            return Optional.of(user);
        }
    }

    /**
     * 统计用户数量
     * @return 用户数量
     * @throws IOException 查询异常
     */
    @Override
    public long countUsers() throws IOException {
        try (Table table = connection.getTable(TableName.valueOf(properties.getTableUsers()));
             ResultScanner scanner = table.getScanner(new Scan())) {
            long count = 0;
            for (Result ignored : scanner) {
                count++;
            }
            return count;
        }
    }
}
