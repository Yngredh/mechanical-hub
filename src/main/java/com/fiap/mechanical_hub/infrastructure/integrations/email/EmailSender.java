package com.fiap.mechanical_hub.infrastructure.integrations.email;

import com.fiap.mechanical_hub.application.dto.notification.NotificationMessage;
import com.fiap.mechanical_hub.application.interfaces.AlertNotificationTrigger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailSender implements AlertNotificationTrigger {

    public boolean send(NotificationMessage message) {
        log.info("Enviando email com assunto: {} e corpo: {}", message.subject(), message.body());
        return true;
    }
}