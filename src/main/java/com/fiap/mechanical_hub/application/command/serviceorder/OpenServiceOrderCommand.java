package com.fiap.mechanical_hub.application.command.serviceorder;

import java.util.List;
import java.util.UUID;

public record OpenServiceOrderCommand(
    UUID customerId,
    UUID vehicleId,
    List<UUID> serviceIds,
    String requestDescription,
    UUID createdByUserId
) { }

