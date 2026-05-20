package com.fiap.mechanical_hub.application.command.stock;

import java.util.UUID;

public record DeleteStockCommand(
    UUID materialId
) { }

