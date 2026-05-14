package com.fiap.mechanical_hub.application.command;

public record FindOrCreateCustomerCommand(
    String name,
    String documentType,
    String documentNumber,
    String telephone,
    String email,
    String address
) { }

