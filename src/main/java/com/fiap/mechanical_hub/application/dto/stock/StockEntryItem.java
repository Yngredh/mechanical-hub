package com.fiap.mechanical_hub.application.dto.stock;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record StockEntryItem(
        @NotNull(message = "Material ID é obrigatório")
        UUID materialId,

        @NotNull(message = "Quantidade é obrigatória")
        @Positive(message = "Quantidade deve ser maior que zero")
        Integer quantity
) { }

