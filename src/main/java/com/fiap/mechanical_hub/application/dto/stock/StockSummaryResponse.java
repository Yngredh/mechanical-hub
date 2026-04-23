package com.fiap.mechanical_hub.application.dto.stock;

import java.util.UUID;

public record StockSummaryResponse(
        UUID materialId,
        String materialName,
        Integer quantityTotal,
        Integer quantityAvailable,
        Integer quantityReserved
) {
}

