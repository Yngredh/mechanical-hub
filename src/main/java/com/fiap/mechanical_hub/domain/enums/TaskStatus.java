package com.fiap.mechanical_hub.domain.enums;

import lombok.Getter;

@Getter
public enum TaskStatus {
    PENDENTE("Pendente"),
    APROVADO("Aprovado"),
    RECUSADO("Recusado"),
    INICIADO("Iniciado"),
    FINALIZADO("Finalizado");

    private final String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

}

