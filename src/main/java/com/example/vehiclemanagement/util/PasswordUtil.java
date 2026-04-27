package com.example.vehiclemanagement.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 密码工具类，提供密码哈希功能。
 */
public final class PasswordUtil {
    private PasswordUtil() {
    }

    /**
     * 使用 SHA-256 算法对输入进行哈希
     * @param input 输入字符串
     * @return 哈希后的字符串
     */
    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    /**
     * 校验存储密码与明文密码是否匹配。
     * 兼容历史明文存储和当前 SHA-256 哈希存储。
     * @param storedPassword HBase 中存储的密码
     * @param rawPassword 用户输入的明文密码
     * @return 是否匹配
     */
    public static boolean matchesStoredPassword(String storedPassword, String rawPassword) {
        if (storedPassword == null || rawPassword == null) {
            return false;
        }
        return storedPassword.equals(rawPassword) || storedPassword.equals(sha256(rawPassword));
    }
}
