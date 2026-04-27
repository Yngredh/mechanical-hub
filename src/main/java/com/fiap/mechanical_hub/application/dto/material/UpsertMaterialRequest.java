package com.fiap.mechanical_hub.application.dto.material;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record UpsertMaterialRequest(
        @NotBlank(message = "Nome do material é obrigatório")
        String name,

        @NotBlank(message = "Descrição do material é obrigatória")
        String description,

        @NotNull(message = "Preço unitário é obrigatório")
        @PositiveOrZero(message = "Preço unitário não pode ser negativo")
        BigDecimal unitPrice,

        @NotNull(message = "Quantidade mínima é obrigatória")
        @Min(value = 1, message = "Quantidade mínima não pode ser negativa")
        Integer minStockQuantity

) { }

