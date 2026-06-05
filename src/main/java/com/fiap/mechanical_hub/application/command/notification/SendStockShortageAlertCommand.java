package com.fiap.mechanical_hub.application.command.notification;

public record SendStockShortageAlertCommand(
        String materialName,
        String orderNumber
) { }
