package com.fiap.mechanical_hub.domain.enums;

public enum StockStatusEnum {
    AVAILABLE("Disponível"),
    RESERVED("Reservado");

    private final String description;

    StockStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

