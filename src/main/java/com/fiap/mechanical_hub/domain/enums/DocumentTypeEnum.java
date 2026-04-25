package com.fiap.mechanical_hub.domain.enums;

import lombok.Getter;

@Getter
public enum DocumentTypeEnum {
    CPF("CPF"),
    CNPJ("CNPJ");

    private final String value;

    DocumentTypeEnum(String value) {
        this.value = value;
    }

    public static DocumentTypeEnum fromValue(String value) {
        for (DocumentTypeEnum type : DocumentTypeEnum.values()) {
            if (type.value.equalsIgnoreCase(value)) { return type; }
        }
        throw new IllegalArgumentException("Invalid document type: " + value);
    }
}

