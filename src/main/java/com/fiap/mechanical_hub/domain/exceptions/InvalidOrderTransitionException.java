package com.fiap.mechanical_hub.domain.exceptions;

public class InvalidOrderTransitionException extends BusinessRuleException {
    public InvalidOrderTransitionException(String message) {
        super(message);
    }
}
