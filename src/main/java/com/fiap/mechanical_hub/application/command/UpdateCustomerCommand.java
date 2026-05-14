package com.fiap.mechanical_hub.application.command;

import java.util.UUID;

public record UpdateCustomerCommand(
    UUID id,
    String name,
    String telephone,
    String email,
    String address
) { }

