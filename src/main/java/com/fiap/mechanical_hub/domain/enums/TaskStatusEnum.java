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

    public static TaskStatusEnum fromString(String value) {
        for (TaskStatusEnum status : TaskStatusEnum.values()) {
            if (status.displayName.equalsIgnoreCase(value) || status.name().equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid order status: " + value);
    }

}

