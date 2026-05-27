package com.fiap.mechanical_hub.application.command.serviceorder;

public record FindByOrderNumberCommand(
    String orderNumber
) { }

