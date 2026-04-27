package com.example.vehiclemanagement.repository;

import com.example.vehiclemanagement.config.HBaseProperties;
import com.example.vehiclemanagement.model.Vehicle;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * HBase 车辆仓库实现
 * 使用 HBase 存储和查询车辆信息
 */
@Repository
public class HBaseVehicleRepository implements VehicleRepository {
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
    public HBaseVehicleRepository(Connection connection, HBaseProperties properties) {
        this.connection = connection;
        this.properties = properties;
    }

    /**
     * 保存车辆信息
     * @param vehicle 车辆信息
     * @throws IOException 存储异常
     */
    @Override
    public void save(Vehicle vehicle) throws IOException {
        try (Table table = connection.getTable(TableName.valueOf(properties.getTableVehicles()))) {
            Put put = new Put(Bytes.toBytes(vehicle.getVehicleId()));
            put.addColumn(CF, Bytes.toBytes("vehicleId"), Bytes.toBytes(vehicle.getVehicleId()));
            put.addColumn(CF, Bytes.toBytes("plateNumber"), Bytes.toBytes(vehicle.getPlateNumber()));
            put.addColumn(CF, Bytes.toBytes("vin"), Bytes.toBytes(nullToEmpty(vehicle.getVin())));
            put.addColumn(CF, Bytes.toBytes("engineNumber"), Bytes.toBytes(nullToEmpty(vehicle.getEngineNumber())));
            put.addColumn(CF, Bytes.toBytes("brand"), Bytes.toBytes(vehicle.getBrand()));
            put.addColumn(CF, Bytes.toBytes("model"), Bytes.toBytes(vehicle.getModel()));
            put.addColumn(CF, Bytes.toBytes("ownerName"), Bytes.toBytes(vehicle.getOwnerName()));
            put.addColumn(CF, Bytes.toBytes("phone"), Bytes.toBytes(vehicle.getPhone()));
            put.addColumn(CF, Bytes.toBytes("status"), Bytes.toBytes(vehicle.getStatus()));
            put.addColumn(CF, Bytes.toBytes("registerDate"), Bytes.toBytes(nullToEmpty(vehicle.getRegisterDate())));
            put.addColumn(CF, Bytes.toBytes("annualInspectionDate"), Bytes.toBytes(nullToEmpty(vehicle.getAnnualInspectionDate())));
            put.addColumn(CF, Bytes.toBytes("insuranceExpireDate"), Bytes.toBytes(nullToEmpty(vehicle.getInsuranceExpireDate())));
            put.addColumn(CF, Bytes.toBytes("mileage"), Bytes.toBytes(vehicle.getMileage()));
            put.addColumn(CF, Bytes.toBytes("remark"), Bytes.toBytes(nullToEmpty(vehicle.getRemark())));
            put.addColumn(CF, Bytes.toBytes("createdBy"), Bytes.toBytes(nullToEmpty(vehicle.getCreatedBy())));
            put.addColumn(CF, Bytes.toBytes("createdAt"), Bytes.toBytes(vehicle.getCreatedAt()));
            put.addColumn(CF, Bytes.toBytes("updatedAt"), Bytes.toBytes(vehicle.getUpdatedAt()));
            table.put(put);
        }
    }

    /**
     * 根据车辆ID查询车辆
     * @param vehicleId 车辆ID
     * @return 车辆信息，不存在则返回 Optional.empty()
     * @throws IOException 查询异常
     */
    @Override
    public Optional<Vehicle> findById(String vehicleId) throws IOException {
        try (Table table = connection.getTable(TableName.valueOf(properties.getTableVehicles()))) {
            Result result = table.get(new Get(Bytes.toBytes(vehicleId)));
            if (result.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(resultToVehicle(result));
        }
    }

    /**
     * 查询所有车辆
     * @return 车辆列表
     * @throws IOException 查询异常
     */
    @Override
    public List<Vehicle> findAll() throws IOException {
        try (Table table = connection.getTable(TableName.valueOf(properties.getTableVehicles()));
             ResultScanner scanner = table.getScanner(new Scan())) {
            List<Vehicle> vehicles = new ArrayList<>();
            for (Result result : scanner) {
                vehicles.add(resultToVehicle(result));
            }
            return vehicles;
        }
    }

    /**
     * 根据品牌查询车辆
     * @param brand 品牌
     * @return 车辆列表
     * @throws IOException 查询异常
     */
    @Override
    public List<Vehicle> findByBrand(String brand) throws IOException {
        List<Vehicle> all = findAll();
        List<Vehicle> filtered = new ArrayList<>();
        for (Vehicle vehicle : all) {
            if (brand.equalsIgnoreCase(vehicle.getBrand())) {
                filtered.add(vehicle);
            }
        }
        return filtered;
    }

    /**
     * 根据状态查询车辆
     * @param status 状态
     * @return 车辆列表
     * @throws IOException 查询异常
     */
    @Override
    public List<Vehicle> findByStatus(String status) throws IOException {
        List<Vehicle> all = findAll();
        List<Vehicle> filtered = new ArrayList<>();
        for (Vehicle vehicle : all) {
            if (status.equalsIgnoreCase(vehicle.getStatus())) {
                filtered.add(vehicle);
            }
        }
        return filtered;
    }

    @Override
    public Optional<Vehicle> findByPlateNumber(String plateNumber) throws IOException {
        return findAll().stream()
                .filter(vehicle -> plateNumber.equalsIgnoreCase(vehicle.getPlateNumber()))
                .findFirst();
    }

    @Override
    public Optional<Vehicle> findByVin(String vin) throws IOException {
        return findAll().stream()
                .filter(vehicle -> vin.equalsIgnoreCase(vehicle.getVin()))
                .findFirst();
    }

    /**
     * 根据车辆ID删除车辆
     * @param vehicleId 车辆ID
     * @throws IOException 删除异常
     */
    @Override
    public void deleteById(String vehicleId) throws IOException {
        try (Table table = connection.getTable(TableName.valueOf(properties.getTableVehicles()))) {
            table.delete(new Delete(Bytes.toBytes(vehicleId)));
        }
    }

    /**
     * 将 HBase Result 转换为 Vehicle 对象
     * @param result HBase 查询结果
     * @return Vehicle 对象
     */
    private Vehicle resultToVehicle(Result result) {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleId(stringValue(result, "vehicleId", Bytes.toString(result.getRow())));
        vehicle.setPlateNumber(stringValue(result, "plateNumber", ""));
        vehicle.setVin(stringValue(result, "vin", ""));
        vehicle.setEngineNumber(stringValue(result, "engineNumber", ""));
        vehicle.setBrand(stringValue(result, "brand", ""));
        vehicle.setModel(stringValue(result, "model", ""));
        vehicle.setOwnerName(stringValue(result, "ownerName", ""));
        vehicle.setPhone(stringValue(result, "phone", ""));
        vehicle.setStatus(stringValue(result, "status", "ACTIVE"));
        vehicle.setRegisterDate(stringValue(result, "registerDate", ""));
        vehicle.setAnnualInspectionDate(stringValue(result, "annualInspectionDate", ""));
        vehicle.setInsuranceExpireDate(stringValue(result, "insuranceExpireDate", ""));
        vehicle.setRemark(stringValue(result, "remark", ""));
        vehicle.setCreatedBy(stringValue(result, "createdBy", ""));

        byte[] mileage = result.getValue(CF, Bytes.toBytes("mileage"));
        byte[] createdAt = result.getValue(CF, Bytes.toBytes("createdAt"));
        byte[] updatedAt = result.getValue(CF, Bytes.toBytes("updatedAt"));
        vehicle.setMileage(longValue(mileage, 0L));
        vehicle.setCreatedAt(longValue(createdAt, 0L));
        vehicle.setUpdatedAt(longValue(updatedAt, 0L));
        return vehicle;
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private String stringValue(Result result, String qualifier, String fallback) {
        byte[] value = result.getValue(CF, Bytes.toBytes(qualifier));
        String text = Bytes.toString(value);
        return text == null || text.isBlank() ? fallback : text;
    }

    private long longValue(byte[] value, long fallback) {
        if (value == null) {
            return fallback;
        }
        try {
            return Bytes.toLong(value);
        } catch (Exception ignored) {
            try {
                return Long.parseLong(Bytes.toString(value));
            } catch (Exception ignoredAgain) {
                return fallback;
            }
        }
    }
}
