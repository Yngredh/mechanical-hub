package com.fiap.mechanical_hub.domain.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    RECEBIDA("Recebida"),
    CRIADA("Criada"),
    EM_DIAGNOSTICO("Em diagnóstico"),
    EM_EXECUCAO("Em execução"),
    APROVADO("Aprovado"),
    RECUSADO("Recusado"),
    FINALIZADO("Finalizado"),
    ENTREGUE("Entregue"),
    CANCELADO("Cancelado");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public static OrderStatus fromString(String value) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.displayName.equalsIgnoreCase(value) || status.name().equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid order status: " + value);
    }
}

