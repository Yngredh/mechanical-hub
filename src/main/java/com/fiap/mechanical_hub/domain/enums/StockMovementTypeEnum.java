package com.fiap.mechanical_hub.domain.enums;

import lombok.Getter;

@Getter
public enum StockMovementTypeEnum {
    ENTRY("ENTRADA"),
    RESERVED("RESERVADO"),
    RETURN("RETORNO");

    private final String description;

    StockMovementTypeEnum(String description) {
        this.description = description;
    }

}
