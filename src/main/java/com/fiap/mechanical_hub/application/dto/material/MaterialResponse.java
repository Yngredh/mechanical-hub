package com.fiap.mechanical_hub.application.dto.material;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record MaterialResponse (
        UUID id,
        String name,
        String description,
        BigDecimal unitPrice,
        Integer minStockQuantity,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){}