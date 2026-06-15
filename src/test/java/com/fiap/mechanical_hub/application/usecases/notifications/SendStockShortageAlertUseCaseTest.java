package com.fiap.mechanical_hub.application.usecases.notifications;

import com.fiap.mechanical_hub.application.command.notification.SendStockShortageAlertCommand;
import com.fiap.mechanical_hub.domain.interfaces.AlertNotificationTrigger;
import com.fiap.mechanical_hub.domain.utils.NotificationMessage;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SendStockShortageAlertUseCaseTest {

    private static final String MATERIAL_NAME = "Pastilha de Freio";
    private static final String ORDER_NUMBER = "OS-2024-001";

    private final AlertNotificationTrigger alertNotificationTrigger = mock(AlertNotificationTrigger.class);
    private final SendStockShortageAlertUseCase useCase = new SendStockShortageAlertUseCase(alertNotificationTrigger);

    @Test
    void shouldCallSend_whenCommandIsValid() {
        SendStockShortageAlertCommand command = new SendStockShortageAlertCommand(MATERIAL_NAME, ORDER_NUMBER);

        useCase.execute(command);

        verify(alertNotificationTrigger).send(ArgumentMatchers.any(NotificationMessage.class));
    }

    @Test
    void shouldBuildBodyWithMaterialNameAndOrderNumber_whenSendingStockShortageAlert() {
        SendStockShortageAlertCommand command = new SendStockShortageAlertCommand(MATERIAL_NAME, ORDER_NUMBER);
        ArgumentCaptor<NotificationMessage> captor = ArgumentCaptor.forClass(NotificationMessage.class);

        useCase.execute(command);

        verify(alertNotificationTrigger).send(captor.capture());
        assertThat(captor.getValue().body()).contains(MATERIAL_NAME);
        assertThat(captor.getValue().body()).contains(ORDER_NUMBER);
    }

}
