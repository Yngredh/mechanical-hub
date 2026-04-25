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

    public static TaskStatus fromString(String value) {
        for (TaskStatus status : TaskStatus.values()) {
            if (status.displayName.equalsIgnoreCase(value) || status.name().equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid task status: " + value);
    }
}
