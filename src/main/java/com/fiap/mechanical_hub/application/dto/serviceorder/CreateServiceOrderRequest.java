package com.fiap.mechanical_hub.application.dto.serviceorder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateServiceOrderRequest {
    private CustomerData customer;
    private VehicleData vehicle;
    private String requestDescription;
}
