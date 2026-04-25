package com.fiap.mechanical_hub.application.dto.serviceorder;

import com.fiap.mechanical_hub.domain.entities.Customer;
import com.fiap.mechanical_hub.domain.entities.Vehicle;
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
    private Customer customer;
    private Vehicle vehicle;
    private BigDecimal budget;
    private LocalDateTime createdAt;
}
