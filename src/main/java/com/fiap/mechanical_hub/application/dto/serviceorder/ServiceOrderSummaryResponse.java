package com.fiap.mechanical_hub.application.dto.serviceorder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOrderSummaryResponse {
    private UUID id;
    private String orderNumber;
    private String status;
    private boolean hasStockPending;
    private LocalDateTime createdAt;
}
