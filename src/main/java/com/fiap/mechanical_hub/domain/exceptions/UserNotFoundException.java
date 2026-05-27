package com.fiap.mechanical_hub.domain.exceptions;

public class UserNotFoundException extends BusinessRuleException {
    public UserNotFoundException(String id) {
        super("User not found: " + id);
    }
}

