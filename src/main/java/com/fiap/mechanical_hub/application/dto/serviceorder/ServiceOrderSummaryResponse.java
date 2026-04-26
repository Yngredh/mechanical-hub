package com.fiap.mechanical_hub.application.dto.serviceorder;

import com.fiap.mechanical_hub.application.dto.customer.CustomerResponse;
import com.fiap.mechanical_hub.application.dto.vehicle.VehicleResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOrderSummaryResponse {

    private UUID id;
    private String orderNumber;
    private String status;
    private CustomerResponse customer;
    private VehicleResponse vehicle;
    private BigDecimal budget;
    private LocalDateTime createdAt;
}
