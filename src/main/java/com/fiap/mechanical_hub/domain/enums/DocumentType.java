package com.fiap.mechanical_hub.domain.enums;

import lombok.Getter;

@Getter
public enum DocumentType {
    CPF("CPF"),
    CNPJ("CNPJ");

    private final String value;

    DocumentType(String value) {
        this.value = value;
    }

    public static DocumentType fromValue(String value) {
        for (DocumentType type : DocumentType.values()) {
            if (type.value.equalsIgnoreCase(value)) { return type; }
        }
        throw new IllegalArgumentException("Invalid document type: " + value);
    }
}

