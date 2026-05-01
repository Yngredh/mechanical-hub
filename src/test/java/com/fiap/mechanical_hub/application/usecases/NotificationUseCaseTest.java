package com.fiap.mechanical_hub.application.usecases;

import com.fiap.mechanical_hub.application.dto.notification.NotificationMessage;
import com.fiap.mechanical_hub.application.interfaces.AlertNotificationTrigger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class NotificationUseCaseTest {

    @Mock
    private AlertNotificationTrigger alertNotificationTrigger;

    @InjectMocks
    private NotificationUseCase notificationUseCase;

    @Test
    @DisplayName("Deve enviar alerta de estoque baixo com formato correto")
    void shouldSendLowStockAlertWithCorrectFormat() {
        String materialName = "Pastilha de Freio";
        Integer minStock = 5;

        notificationUseCase.sendLowStockAlert(materialName, minStock);

        ArgumentCaptor<NotificationMessage> messageCaptor = ArgumentCaptor.forClass(NotificationMessage.class);
        verify(alertNotificationTrigger, times(1)).send(messageCaptor.capture());

        NotificationMessage capturedMessage = messageCaptor.getValue();

        assertThat(capturedMessage.subject()).contains("[ALERTA ESTOQUE]", materialName);
        assertThat(capturedMessage.body())
                .contains(materialName)
                .contains(minStock.toString())
                .contains("estoque mínimo");
    }

    @Test
    @DisplayName("Deve enviar alerta de falta de estoque (pendência) com formato correto")
    void shouldSendStockShortageAlertWithCorrectFormat() {
        String materialName = "Óleo 5W30";
        String orderNumber = "OS-2024-001";

        notificationUseCase.sendStockShortageAlert(materialName, orderNumber);

        ArgumentCaptor<NotificationMessage> messageCaptor = ArgumentCaptor.forClass(NotificationMessage.class);
        verify(alertNotificationTrigger, times(1)).send(messageCaptor.capture());

        NotificationMessage capturedMessage = messageCaptor.getValue();

        assertThat(capturedMessage.subject()).contains("Material sem disponibilidade", materialName);
        assertThat(capturedMessage.body())
                .contains(materialName)
                .contains(orderNumber)
                .contains("pendência de estoque");
    }
}