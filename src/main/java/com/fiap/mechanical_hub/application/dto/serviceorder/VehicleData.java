package com.fiap.mechanical_hub.application.dto.serviceorder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleData {
    private String licensePlate;
    private String brand;
    private String model;
    private Integer year;
    private String color;
}
