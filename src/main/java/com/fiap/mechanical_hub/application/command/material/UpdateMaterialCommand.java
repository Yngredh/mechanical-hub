package com.fiap.mechanical_hub.application.command.material;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateMaterialCommand(
    UUID id,
    String name,
    String description,
    BigDecimal unitPrice,
    Integer minStockQuantity
) { }

