package com.fiap.mechanical_hub.domain.exceptions;

public class DuplicateLicensePlateException extends BusinessRuleException {
    public DuplicateLicensePlateException(String message) {
        super(message);
    }
}

