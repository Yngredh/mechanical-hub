package com.fiap.mechanical_hub.domain.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    RECEBIDA("Recebida"),
    EM_DIAGNOSTICO("Em Diagnóstico"),
    AGUARDANDO_APROVACAO("Aguardando Aprovação"),
    APROVADO("Aprovado"),
    RECUSADO("Recusado"),
    EM_EXECUCAO("Em Execução"),
    FINALIZADA("Finalizada"),
    ENTREGUE("Entregue"),
    CANCELADA("Cancelada");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }
}
