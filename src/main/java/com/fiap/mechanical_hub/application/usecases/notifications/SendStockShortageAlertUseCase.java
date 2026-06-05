package com.fiap.mechanical_hub.application.usecases.notifications;

import com.fiap.mechanical_hub.application.command.notification.SendStockShortageAlertCommand;
import com.fiap.mechanical_hub.domain.interfaces.AlertNotificationTrigger;
import com.fiap.mechanical_hub.domain.utils.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SendStockShortageAlertUseCase {

    private final AlertNotificationTrigger alertNotificationTrigger;

    public void execute(SendStockShortageAlertCommand command) {
        log.info("Sending stock shortage alert for material: {} on order: {}", command.materialName(), command.orderNumber());

        String subject = "[ALERTA ESTOQUE] Material sem disponibilidade: " + command.materialName();
        String body = """
    O estoque do material %s disponível para a ordem %s..

Não há unidades disponíveis para atender a demanda atual, e a ordem foi marcada com pendência de estoque.

    Por favor, providencie a reposição para permitir a continuidade da execução.
""".formatted(command.materialName(), command.orderNumber());

        NotificationMessage message = new NotificationMessage(subject, body);
        alertNotificationTrigger.send(message);

        log.info("Stock shortage alert sent for material: {} on order: {}", command.materialName(), command.orderNumber());
    }
}
