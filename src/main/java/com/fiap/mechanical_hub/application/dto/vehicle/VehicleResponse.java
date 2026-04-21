package com.fiap.mechanical_hub.application.dto.vehicle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleResponse {
    private UUID id;
    private UUID customerId;
    private String licensePlate;
    private String brand;
    private String model;
    private Integer year;
    private String color;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

