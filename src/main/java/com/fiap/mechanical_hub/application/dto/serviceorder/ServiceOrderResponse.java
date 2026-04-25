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
public class ServiceOrderResponse {
    private UUID id;
    private String orderNumber;
    private String orderStatus;
    private String requestDescription;
    private BigDecimal budget;
    private boolean hasStockPending;
    private UUID responsibleUserId;
    private CustomerResponse customer;
    private VehicleResponse vehicle;
    private LocalDateTime estimatedCompletionAt;
    private LocalDateTime openedAt;
    private LocalDateTime completedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
