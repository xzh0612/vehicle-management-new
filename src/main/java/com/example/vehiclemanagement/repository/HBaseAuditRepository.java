package com.example.vehiclemanagement.repository;

import com.example.vehiclemanagement.config.HBaseProperties;
import com.example.vehiclemanagement.model.AuditRecord;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * HBase 审计记录仓库实现
 * 使用 HBase 存储和查询审计记录
 */
@Repository
public class HBaseAuditRepository implements AuditRepository {
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
    public HBaseAuditRepository(Connection connection, HBaseProperties properties) {
        this.connection = connection;
        this.properties = properties;
    }

    /**
     * 保存审计记录
     * @param record 审计记录
     * @throws IOException 存储异常
     */
    @Override
    public void save(AuditRecord record) throws IOException {
        try (Table table = connection.getTable(TableName.valueOf(properties.getTableAudit()))) {
            Put put = new Put(Bytes.toBytes(record.getRecordId()));
            put.addColumn(CF, Bytes.toBytes("recordId"), Bytes.toBytes(record.getRecordId()));
            put.addColumn(CF, Bytes.toBytes("vehicleId"), Bytes.toBytes(record.getVehicleId()));
            put.addColumn(CF, Bytes.toBytes("operation"), Bytes.toBytes(record.getOperation()));
            put.addColumn(CF, Bytes.toBytes("operator"), Bytes.toBytes(record.getOperator()));
            put.addColumn(CF, Bytes.toBytes("detail"), Bytes.toBytes(record.getDetail() == null ? "" : record.getDetail()));
            put.addColumn(CF, Bytes.toBytes("timestamp"), Bytes.toBytes(record.getTimestamp()));
            table.put(put);
        }
    }

    /**
     * 根据车辆ID查询审计记录
     * @param vehicleId 车辆ID
     * @return 审计记录列表，按时间戳倒序排序
     * @throws IOException 查询异常
     */
    @Override
    public List<AuditRecord> findByVehicleId(String vehicleId) throws IOException {
        try (Table table = connection.getTable(TableName.valueOf(properties.getTableAudit()));
             ResultScanner scanner = table.getScanner(new Scan())) {
            List<AuditRecord> records = new ArrayList<>();
            for (Result result : scanner) {
                String currentVehicleId = Bytes.toString(result.getValue(CF, Bytes.toBytes("vehicleId")));
                if (!vehicleId.equals(currentVehicleId)) {
                    continue;
                }
                AuditRecord record = new AuditRecord();
                record.setRecordId(stringValue(result, "recordId", Bytes.toString(result.getRow())));
                record.setVehicleId(currentVehicleId);
                record.setOperation(stringValue(result, "operation", stringValue(result, "action", "UNKNOWN")));
                record.setOperator(stringValue(result, "operator", ""));
                record.setDetail(stringValue(result, "detail", ""));
                byte[] ts = result.getValue(CF, Bytes.toBytes("timestamp"));
                record.setTimestamp(parseTimestamp(ts));
                records.add(record);
            }
            records.sort(Comparator.comparingLong(AuditRecord::getTimestamp).reversed());
            return records;
        }
    }

    private String stringValue(Result result, String qualifier, String fallback) {
        byte[] value = result.getValue(CF, Bytes.toBytes(qualifier));
        String text = Bytes.toString(value);
        return text == null || text.isBlank() ? fallback : text;
    }

    private long parseTimestamp(byte[] value) {
        if (value == null) {
            return 0L;
        }
        try {
            return Bytes.toLong(value);
        } catch (Exception ignored) {
            String text = Bytes.toString(value);
            if (text == null || text.isBlank()) {
                return 0L;
            }
            try {
                return Long.parseLong(text);
            } catch (Exception ignoredAgain) {
                try {
                    return Instant.parse(text).toEpochMilli();
                } catch (Exception ignoredThird) {
                    return 0L;
                }
            }
        }
    }
}
