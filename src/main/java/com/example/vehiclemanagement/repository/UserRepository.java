package com.example.vehiclemanagement.repository;

import com.example.vehiclemanagement.model.User;

import java.io.IOException;
import java.util.Optional;

/**
 * 用户仓库接口
 * 定义用户的存储和查询方法
 */
public interface UserRepository {
    /**
     * 保存用户信息
     * @param user 用户信息
     * @throws IOException 存储异常
     */
    void save(User user) throws IOException;

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息，不存在则返回 Optional.empty()
     * @throws IOException 查询异常
     */
    Optional<User> findByUsername(String username) throws IOException;

    /**
     * 统计用户数量
     * @return 用户数量
     * @throws IOException 查询异常
     */
    long countUsers() throws IOException;
}
