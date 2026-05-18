package com.fiap.mechanical_hub.application.command.material;

import java.math.BigDecimal;

public record CreateMaterialCommand(
    String name,
    String description,
    BigDecimal unitPrice,
    Integer minStockQuantity
) { }

