package com.fiap.mechanical_hub.application.command.serviceorder;

import java.util.UUID;

public record RejectServiceOrderCommand(
    UUID serviceOrderId
) { }

