package com.fiap.mechanical_hub.domain.interfaces;

import com.fiap.mechanical_hub.application.dto.notification.NotificationMessage;

public interface AlertNotificationTrigger {
    boolean send(NotificationMessage message);
}
