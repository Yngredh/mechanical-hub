package com.fiap.mechanical_hub.domain.exceptions;

public class CustomerNotFoundException extends BusinessRuleException {
    public CustomerNotFoundException(String id) {
        super("Cliente não encontrado com id: " + id);
    }
}

