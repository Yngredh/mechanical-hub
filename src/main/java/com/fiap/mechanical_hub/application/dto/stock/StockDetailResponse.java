package com.fiap.mechanical_hub.application.dto.stock;

import java.util.List;
import java.util.UUID;

public record StockDetailResponse(
        UUID materialId,
        Integer quantityTotal,
        Integer quantityAvailable,
        Integer quantityReserved,
        List<StockMovementResponse> movements
) {
}

