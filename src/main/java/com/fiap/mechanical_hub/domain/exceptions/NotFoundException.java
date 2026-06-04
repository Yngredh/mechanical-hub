package com.fiap.mechanical_hub.domain.exceptions;

public class NotFoundException extends BusinessRuleException {
    public NotFoundException(String message) {
        super(message);
    }
}

