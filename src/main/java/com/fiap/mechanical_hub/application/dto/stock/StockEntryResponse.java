package com.fiap.mechanical_hub.application.dto.stock;

import java.time.LocalDateTime;
import java.util.UUID;

public record StockEntryResponse(
        UUID materialId,
        Integer quantityAdded,
        String status,
        LocalDateTime timestamp
) { }

