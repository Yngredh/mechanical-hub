package com.fiap.mechanical_hub.domain.enums;

import lombok.Getter;

@Getter
public enum TaskStatus {
    PENDENTE("Pendente"),
    INICIADO("Iniciado"),
    FINALIZADO("Finalizado"),
    CANCELADO("Cancelado");

    private final String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

}

