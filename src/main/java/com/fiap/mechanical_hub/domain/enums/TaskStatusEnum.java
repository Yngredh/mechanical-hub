package com.fiap.mechanical_hub.domain.enums;

import lombok.Getter;

@Getter
public enum TaskStatusEnum {
    PENDENTE("Pendente"),
    INICIADO("Iniciado"),
    FINALIZADO("Finalizado");

    private final String displayName;

    TaskStatusEnum(String displayName) {
        this.displayName = displayName;
    }

}

