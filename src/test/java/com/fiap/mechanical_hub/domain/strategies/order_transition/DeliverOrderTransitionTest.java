package com.fiap.mechanical_hub.domain.strategies.order_transition;

import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;
import com.fiap.mechanical_hub.domain.exceptions.InvalidOrderTransitionException;
import com.fiap.mechanical_hub.domain.strategies.order_transition.mocks.ServiceOrderMock;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DeliverOrderTransitionTest {

    private final DeliverOrderTransition transition = new DeliverOrderTransition();
    private final UUID userId = UUID.randomUUID();

    @Test
    void shouldDeliverOrderWhenStatusIsFinished() {
        ServiceOrder order = ServiceOrderMock.finishedOrder();

        transition.execute(order, userId);

        assertEquals(OrderStatusEnum.ENTREGUE, order.getStatus());
        assertNotNull(order.getDeliveredAt());
    }

    @Test
    void shouldThrowExceptionWhenDeliveringFromNonFinishedStatus() {
        ServiceOrder order = ServiceOrderMock.inExecutionOrderWithTasks(java.util.List.of());

        InvalidOrderTransitionException exception = assertThrows(
                InvalidOrderTransitionException.class,
                () -> transition.execute(order, userId)
        );

        assertTrue(exception.getMessage().contains("Invalid transition"));
        assertEquals(OrderStatusEnum.EM_EXECUCAO, order.getStatus());
    }
}

