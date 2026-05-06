package com.fiap.mechanical_hub.domain.strategies.order_transition;

import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;
import com.fiap.mechanical_hub.domain.exceptions.InvalidOrderTransitionException;
import com.fiap.mechanical_hub.domain.strategies.order_transition.mocks.ServiceOrderMock;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class StartDiagnosisTransitionTest {

    private final StartDiagnosisTransition transition = new StartDiagnosisTransition();
    private final UUID userId = UUID.randomUUID();

    @Test
    void shouldStartDiagnosisWhenOrderIsReceived() {
        ServiceOrder order = ServiceOrderMock.receivedOrder();

        transition.execute(order, userId);

        assertEquals(OrderStatusEnum.EM_DIAGNOSTICO, order.getStatus());
        assertNotNull(order.getOpenedAt());
    }

    @Test
    void shouldThrowExceptionWhenStartingDiagnosisFromNonReceivedStatus() {
        ServiceOrder order = ServiceOrderMock.inDiagnosisOrderWithoutBudget();

        InvalidOrderTransitionException exception = assertThrows(
                InvalidOrderTransitionException.class,
                () -> transition.execute(order, userId)
        );

        assertTrue(exception.getMessage().contains("Invalid transition"));
    }
}

