package com.fiap.mechanical_hub.application.dto.vehicle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateVehicleRequest {
    private String brand;
    private String model;
    private Integer year;
    private String color;
}
