package com.fiap.mechanical_hub.application.command.serviceorder;

import java.util.UUID;

public record CreateServiceOrderCommand(
    String customerName,
    String documentType,
    String documentNumber,
    String telephone,
    String email,
    String address,
    String licensePlate,
    String vehicleBrand,
    String vehicleModel,
    Integer vehicleYear,
    String vehicleColor,
    String requestDescription,
    UUID createdByUserId
) { }

