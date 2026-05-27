package com.fiap.mechanical_hub.domain.exceptions;

public class DuplicatedDocumentException extends BusinessRuleException {
    public DuplicatedDocumentException(String message) {
        super(message);
    }
}

