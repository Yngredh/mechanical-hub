package com.fiap.mechanical_hub.application.command.vehicle;

import java.util.UUID;

public record UpdateVehicleCommand(
    UUID id,
    String brand,
    String model,
    Integer year,
    String color
) { }

