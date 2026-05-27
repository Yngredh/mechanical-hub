package com.fiap.mechanical_hub.application.command.serviceorder;

import java.util.List;
import java.util.UUID;

public record AddTaskIntoServiceOrderCommand(
    UUID serviceOrderId,
    List<UUID> serviceIds
) { }

