package com.fiap.mechanical_hub.application.dto.stock;

import java.time.LocalDateTime;
import java.util.UUID;

public record StockMovementResponse(
        UUID id,
        UUID materialId,
        UUID serviceOrderId,
        String movementType,
        Integer quantity,
        LocalDateTime createdAt
) {
}

