package com.fiap.mechanical_hub.application.command;

public record CreateCustomerCommand(
    String name,
    String documentType,
    String documentNumber,
    String telephone,
    String email,
    String address
) { }
