package com.fiap.mechanical_hub.application.dto.vehicle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpsertVehicleRequest {
    private String licensePlate;
    private String brand;
    private String model;
    private Integer year;
    private String color;
}