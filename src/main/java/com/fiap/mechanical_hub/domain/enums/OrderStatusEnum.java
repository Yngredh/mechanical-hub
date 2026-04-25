package com.fiap.mechanical_hub.domain.enums;

import lombok.Getter;

@Getter
public enum OrderStatusEnum {
    RECEBIDO("Recebido"),
    EM_DIAGNOSTICO("Em diagnóstico"),
    AGUARDANDO_APROVACAO("Aguardando aprovação"),
    APROVADO("Aprovado"),
    RECUSADO("Recusado"),
    EM_EXECUCAO("Em execução"),
    FINALIZADO("Finalizado"),
    ENTREGUE("Entregue");

    private final String displayName;

    OrderStatusEnum(String displayName) {
        this.displayName = displayName;
    }

    public static OrderStatusEnum fromString(String value) {
        for (OrderStatusEnum status : OrderStatusEnum.values()) {
            if (status.displayName.equalsIgnoreCase(value) || status.name().equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid order status: " + value);
    }
}
