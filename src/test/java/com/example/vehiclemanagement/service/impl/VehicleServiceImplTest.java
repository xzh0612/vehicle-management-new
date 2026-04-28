package com.example.vehiclemanagement.service.impl;

import com.example.vehiclemanagement.dto.PageResponse;
import com.example.vehiclemanagement.dto.VehicleQueryRequest;
import com.example.vehiclemanagement.dto.VehicleRequest;
import com.example.vehiclemanagement.exception.BadRequestException;
import com.example.vehiclemanagement.model.AuditRecord;
import com.example.vehiclemanagement.model.Vehicle;
import com.example.vehiclemanagement.repository.AuditRepository;
import com.example.vehiclemanagement.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehicleServiceImplTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private AuditRepository auditRepository;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    private VehicleRequest request;

    @BeforeEach
    void setUp() {
        request = new VehicleRequest();
        request.setPlateNumber("沪A12345");
        request.setVin("VIN123456789");
        request.setEngineNumber("ENG-001");
        request.setBrand("BYD");
        request.setModel("Han");
        request.setOwnerName("Alice");
        request.setPhone("13800000000");
        request.setStatus("ACTIVE");
        request.setRegisterDate("2026-01-01");
        request.setAnnualInspectionDate("2026-12-01");
        request.setInsuranceExpireDate("2026-11-01");
        request.setMileage(12000L);
        request.setRemark("首保已完成");
    }

    @Test
    void shouldRejectDuplicatePlateNumber() throws IOException {
        Vehicle existing = new Vehicle();
        existing.setVehicleId("existing-id");
        existing.setPlateNumber("沪A12345");
        existing.setVin("OTHER-VIN");
        when(vehicleRepository.findByPlateNumber("沪A12345")).thenReturn(Optional.of(existing));

        assertThrows(BadRequestException.class, () -> vehicleService.addVehicle(request, "admin"));

        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void shouldSaveVehicleAndAuditWhenCreate() throws IOException {
        when(vehicleRepository.findByPlateNumber("沪A12345")).thenReturn(Optional.empty());
        when(vehicleRepository.findByVin("VIN123456789")).thenReturn(Optional.empty());

        Vehicle saved = vehicleService.addVehicle(request, "admin");

        ArgumentCaptor<Vehicle> vehicleCaptor = ArgumentCaptor.forClass(Vehicle.class);
        verify(vehicleRepository).save(vehicleCaptor.capture());
        assertEquals("admin", vehicleCaptor.getValue().getCreatedBy());
        assertEquals("ACTIVE", saved.getStatus());

        ArgumentCaptor<AuditRecord> auditCaptor = ArgumentCaptor.forClass(AuditRecord.class);
        verify(auditRepository).save(auditCaptor.capture());
        assertEquals("CREATE", auditCaptor.getValue().getOperation());
    }

    @Test
    void shouldFilterAndPaginateVehicles() throws IOException {
        Vehicle v1 = vehicle("1", "沪A11111", "Tesla", "ACTIVE", "Alice", 1000, 10);
        Vehicle v2 = vehicle("2", "沪B22222", "BYD", "MAINTENANCE", "Bob", 2000, 20);
        Vehicle v3 = vehicle("3", "沪C33333", "Tesla", "ACTIVE", "Cindy", 3000, 30);
        when(vehicleRepository.findAll()).thenReturn(List.of(v1, v2, v3));

        VehicleQueryRequest query = new VehicleQueryRequest();
        query.setBrand("Tesla");
        query.setStatus("ACTIVE");
        query.setSortBy("mileage");
        query.setSortDir("desc");
        query.setPage(1);
        query.setSize(1);

        PageResponse<Vehicle> page = vehicleService.list(query);

        assertEquals(2, page.getTotal());
        assertEquals(1, page.getItems().size());
        assertEquals("沪C33333", page.getItems().get(0).getPlateNumber());
    }

    private Vehicle vehicle(String id, String plate, String brand, String status, String owner, long mileage, long updatedAt) {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleId(id);
        vehicle.setPlateNumber(plate);
        vehicle.setVin(id + "-VIN");
        vehicle.setBrand(brand);
        vehicle.setModel("Model");
        vehicle.setOwnerName(owner);
        vehicle.setPhone("13800000000");
        vehicle.setStatus(status);
        vehicle.setRegisterDate("2026-01-01");
        vehicle.setAnnualInspectionDate("2026-12-01");
        vehicle.setInsuranceExpireDate("2026-11-01");
        vehicle.setMileage(mileage);
        vehicle.setUpdatedAt(updatedAt);
        return vehicle;
    }
}
