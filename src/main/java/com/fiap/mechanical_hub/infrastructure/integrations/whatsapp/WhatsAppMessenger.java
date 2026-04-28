package com.fiap.mechanical_hub.infrastructure.integrations.whatsapp;

import com.fiap.mechanical_hub.application.interfaces.SendBudgetApproval;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@NoArgsConstructor
public class WhatsAppMessenger implements SendBudgetApproval {

    @Override
    public void sendBudgetApprovalRequest(ServiceOrder os) {
        log.info("Enviando solicitação de aprovação de orçamento para a ordem de serviço {}.", os.getOrderNumber());

        log.info("Orçamento da ordem de serviço {} enviado para aprovação com sucesso.", os.getOrderNumber());
    }

}
