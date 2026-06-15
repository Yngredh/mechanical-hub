package com.fiap.mechanical_hub.domain.interfaces;

import com.fiap.mechanical_hub.domain.utils.NotificationMessage;

public interface AlertNotificationTrigger {
    boolean send(NotificationMessage message);
}
