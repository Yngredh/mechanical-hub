package com.fiap.mechanical_hub.application.usecases;

import com.fiap.mechanical_hub.application.dto.notification.NotificationMessage;
import com.fiap.mechanical_hub.application.interfaces.AlertNotificationTrigger;
import org.springframework.stereotype.Service;

@Service
public class NotificationUseCase {

    private final AlertNotificationTrigger alertNotificationTrigger;

    public NotificationUseCase(AlertNotificationTrigger alertNotificationTrigger) {
        this.alertNotificationTrigger = alertNotificationTrigger;
    }

    public void sendLowStockAlert(String materialName, Integer minStockQuantity) {
        String subject = "[ALERTA ESTOQUE] Estoque Baixo para o item {}";
        String body = """
    O item de estoque %s está abaixo do estoque mínimo (%d unidades).

    Por favor, providencie a reposição o mais rápido possível para evitar pendências nas ordens de serviço.
        """.formatted(materialName, minStockQuantity);

        NotificationMessage message = new NotificationMessage(subject, body);
        alertNotificationTrigger.send(message);
    }

    public void sendStockShortageAlert(String materialName, String orderNumber) {
        String subject = "[ALERTA ESTOQUE] Material sem disponibilidade: " + materialName;
        String body = """
    O estoque do material %s foi esgotado durante o processamento da Ordem de Serviço %s.

Não há unidades disponíveis para atender a demanda atual, e a ordem foi marcada com pendência de estoque.

    Por favor, providencie a reposição para permitir a continuidade da execução.
""".formatted(materialName, orderNumber);

        NotificationMessage message = new NotificationMessage(subject, body);
        alertNotificationTrigger.send(message);
    }
}
