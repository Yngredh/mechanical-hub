package com.fiap.mechanical_hub.application.command.serviceorder;

import java.util.UUID;

public record ApproveServiceOrderCommand(
    UUID serviceOrderId
) { }

