package com.fiap.mechanical_hub.domain.exceptions;

public class MaterialNotFoundException extends BusinessRuleException {
    public MaterialNotFoundException(String id) {
        super("Material não encontrado com id: " + id);
    }
}

