package com.fiap.mechanical_hub.infrastructure.integrations.whatsapp;

import com.fiap.mechanical_hub.application.interfaces.BudgetApprovalTrigger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WhatsAppMessenger implements BudgetApprovalTrigger {

    public WhatsAppMessenger() {}

    @Override
    public void sendBudgetApprovalRequest(String customerId, String orderNumber) {
        log.info("Enviando solicitação de aprovação de orçamento para a ordem de serviço {}.", orderNumber);

        // TODO implementar integração com API do WhatsApp Business para enviar a mensagem
    }

    @Override
    public void budgetApprovalReceived(String orderNumber) {
        boolean customerApproval = orderNumber.charAt(orderNumber.length() - 1) % 2 == 0;
        if (customerApproval) {
            log.info("Recebido: Orçamento aprovado pelo cliente para a ordem de serviço {}", orderNumber);
            // TODO implementar RN-OS-APROVAÇÃO-WHATSAPP
        } else {
            log.info("Recebido: Orçamento recusado pelo cliente para a ordem de serviço {}", orderNumber);
            // TODO implementar RN-OS-RECUSA-ESTOQUE e RN-EST-RETORNO-RECUSA
        }
    }
}
