package com.fiap.mechanical_hub.application.command.ordertask;

import java.util.UUID;

public record DeleteOrderTaskCommand(
    UUID id
) { }

