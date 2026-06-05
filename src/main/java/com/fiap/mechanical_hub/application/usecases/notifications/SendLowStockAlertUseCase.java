package com.fiap.mechanical_hub.application.usecases.notifications;

import com.fiap.mechanical_hub.application.command.notification.SendLowStockAlertCommand;
import com.fiap.mechanical_hub.domain.interfaces.AlertNotificationTrigger;
import com.fiap.mechanical_hub.domain.utils.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SendLowStockAlertUseCase {

    private final AlertNotificationTrigger alertNotificationTrigger;

    public void execute(SendLowStockAlertCommand command) {
        log.info("Sending low stock alert for material: {}", command.materialName());

        String subject = "[ALERTA ESTOQUE] Estoque Baixo para o item %s".formatted(command.materialName());
        String body = """
    O item de estoque %s está abaixo do estoque mínimo (%d unidades).

    Por favor, providencie a reposição o mais rápido possível para evitar pendências nas ordens de serviço.
        """.formatted(command.materialName(), command.minStockQuantity());

        NotificationMessage message = new NotificationMessage(subject, body);
        alertNotificationTrigger.send(message);

        log.info("Low stock alert sent for material: {}", command.materialName());
    }
}
