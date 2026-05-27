package com.fiap.mechanical_hub.domain.exceptions;

public class UserAlreadyDeletedException extends BusinessRuleException {
    public UserAlreadyDeletedException(String id) {
        super("User already deactivated: " + id);
    }
}

