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
            put.addColumn(CF, Bytes.toBytes("brand"), Bytes.toBytes(vehicle.getBrand()));
            put.addColumn(CF, Bytes.toBytes("model"), Bytes.toBytes(vehicle.getModel()));
            put.addColumn(CF, Bytes.toBytes("ownerName"), Bytes.toBytes(vehicle.getOwnerName()));
            put.addColumn(CF, Bytes.toBytes("phone"), Bytes.toBytes(vehicle.getPhone()));
            put.addColumn(CF, Bytes.toBytes("status"), Bytes.toBytes(vehicle.getStatus()));
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
        vehicle.setVehicleId(Bytes.toString(result.getValue(CF, Bytes.toBytes("vehicleId"))));
        vehicle.setPlateNumber(Bytes.toString(result.getValue(CF, Bytes.toBytes("plateNumber"))));
        vehicle.setBrand(Bytes.toString(result.getValue(CF, Bytes.toBytes("brand"))));
        vehicle.setModel(Bytes.toString(result.getValue(CF, Bytes.toBytes("model"))));
        vehicle.setOwnerName(Bytes.toString(result.getValue(CF, Bytes.toBytes("ownerName"))));
        vehicle.setPhone(Bytes.toString(result.getValue(CF, Bytes.toBytes("phone"))));
        vehicle.setStatus(Bytes.toString(result.getValue(CF, Bytes.toBytes("status"))));

        byte[] createdAt = result.getValue(CF, Bytes.toBytes("createdAt"));
        byte[] updatedAt = result.getValue(CF, Bytes.toBytes("updatedAt"));
        vehicle.setCreatedAt(createdAt == null ? 0L : Bytes.toLong(createdAt));
        vehicle.setUpdatedAt(updatedAt == null ? 0L : Bytes.toLong(updatedAt));
        return vehicle;
    }
}
