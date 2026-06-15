package com.fiap.mechanical_hub.application.usecases.notifications;

import com.fiap.mechanical_hub.application.command.notification.SendLowStockAlertCommand;
import com.fiap.mechanical_hub.domain.interfaces.AlertNotificationTrigger;
import com.fiap.mechanical_hub.domain.utils.NotificationMessage;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SendLowStockAlertUseCaseTest {

    private static final String MATERIAL_NAME = "Filtro de Óleo";
    private static final Integer MIN_STOCK_QUANTITY = 5;

    private final AlertNotificationTrigger alertNotificationTrigger = mock(AlertNotificationTrigger.class);
    private final SendLowStockAlertUseCase useCase = new SendLowStockAlertUseCase(alertNotificationTrigger);

    @Test
    void shouldCallSend_whenCommandIsValid() {
        SendLowStockAlertCommand command = new SendLowStockAlertCommand(MATERIAL_NAME, MIN_STOCK_QUANTITY);

        useCase.execute(command);

        verify(alertNotificationTrigger).send(org.mockito.ArgumentMatchers.any(NotificationMessage.class));
    }

    @Test
    void shouldBuildSubjectWithMaterialName_whenSendingLowStockAlert() {
        SendLowStockAlertCommand command = new SendLowStockAlertCommand(MATERIAL_NAME, MIN_STOCK_QUANTITY);
        ArgumentCaptor<NotificationMessage> captor = ArgumentCaptor.forClass(NotificationMessage.class);

        useCase.execute(command);

        verify(alertNotificationTrigger).send(captor.capture());
        assertThat(captor.getValue().subject()).contains(MATERIAL_NAME);
        assertThat(captor.getValue().body()).contains(String.valueOf(MIN_STOCK_QUANTITY));
    }

}
