package com.fiap.mechanical_hub.application.command.notification;

public record SendLowStockAlertCommand(
        String materialName,
        Integer minStockQuantity
) { }
