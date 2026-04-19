package com.fiap.mechanical_hub.domain.enums;

public enum StockStatus {
    AVAILABLE("Disponível"),
    RESERVED("Reservado");

    private final String description;

    StockStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

