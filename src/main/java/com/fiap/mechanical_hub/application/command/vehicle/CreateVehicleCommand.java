package com.fiap.mechanical_hub.application.command.vehicle;

import java.util.UUID;

public record CreateVehicleCommand(
    UUID customerId,
    String licensePlate,
    String brand,
    String model,
    Integer year,
    String color
) { }

