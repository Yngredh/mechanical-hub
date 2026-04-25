package com.fiap.mechanical_hub.application.dto.serviceorder;

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
    private String customerName;
    private String vehicleInfo;
    private BigDecimal budget;
    private LocalDateTime createdAt;
}
