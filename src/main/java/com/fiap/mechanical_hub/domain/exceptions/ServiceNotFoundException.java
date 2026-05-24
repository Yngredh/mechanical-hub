package com.fiap.mechanical_hub.domain.exceptions;

public class ServiceNotFoundException extends BusinessRuleException {
    public ServiceNotFoundException(String id) {
        super("Serviço não encontrado com id: " + id);
    }
}

