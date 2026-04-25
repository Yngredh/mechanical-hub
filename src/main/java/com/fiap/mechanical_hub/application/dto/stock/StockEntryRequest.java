package com.fiap.mechanical_hub.application.dto.stock;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record StockEntryRequest(
        @NotEmpty(message = "Lista de itens não pode estar vazia")
        @Valid
        List<StockEntryItem> items
) { }

