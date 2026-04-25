package com.fiap.mechanical_hub.domain.exceptions;

public class InvalidOrderStatusTransitionException extends BusinessRuleException {
    public InvalidOrderStatusTransitionException(String fromStatus, String toStatus) {
        super("Transição inválida de " + fromStatus + " para " + toStatus);
    }

}

