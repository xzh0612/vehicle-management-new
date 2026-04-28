package com.example.vehiclemanagement.service.impl;

import com.example.vehiclemanagement.config.HdfsProperties;
import com.example.vehiclemanagement.model.Vehicle;
import com.example.vehiclemanagement.repository.VehicleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsServiceImplTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private HdfsProperties hdfsProperties;

    @InjectMocks
    private StatsServiceImpl statsService = new StatsServiceImpl(vehicleRepository, hdfsProperties, new ObjectMapper());

    @Test
    void shouldGenerateDashboardStats() throws IOException {
        LocalDate today = LocalDate.now();
        when(vehicleRepository.findAll()).thenReturn(List.of(
                vehicle("1", "Tesla", "ACTIVE", "admin", today.plusDays(10).toString(), today.minusDays(1).toString()),
                vehicle("2", "BYD", "MAINTENANCE", "admin", today.plusDays(45).toString(), today.plusDays(15).toString())
        ));

        Map<String, Object> stats = statsService.generateStats();

        assertEquals(2, stats.get("total"));
        assertEquals(Map.of("Tesla", 1L, "BYD", 1L), stats.get("byBrand"));
        assertEquals(Map.of("admin", 2L), stats.get("byCreator"));

        @SuppressWarnings("unchecked")
        Map<String, Object> inspection = (Map<String, Object>) stats.get("inspectionReminders");
        @SuppressWarnings("unchecked")
        Map<String, Object> insurance = (Map<String, Object>) stats.get("insuranceReminders");

        assertEquals(1L, inspection.get("expiringWithin30Days"));
        assertEquals(1L, insurance.get("overdue"));
    }

    private Vehicle vehicle(String id, String brand, String status, String creator, String inspectionDate, String insuranceDate) {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleId(id);
        vehicle.setPlateNumber("plate-" + id);
        vehicle.setVin("vin-" + id);
        vehicle.setBrand(brand);
        vehicle.setModel("model");
        vehicle.setOwnerName("owner");
        vehicle.setStatus(status);
        vehicle.setCreatedBy(creator);
        vehicle.setAnnualInspectionDate(inspectionDate);
        vehicle.setInsuranceExpireDate(insuranceDate);
        vehicle.setCreatedAt(Long.parseLong(id));
        return vehicle;
    }
}
