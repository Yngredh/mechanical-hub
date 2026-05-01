package com.fiap.mechanical_hub.domain.strategies.order_transition;

import com.fiap.mechanical_hub.domain.entities.OrderTask;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;
import com.fiap.mechanical_hub.domain.exceptions.InvalidOrderTransitionException;
import com.fiap.mechanical_hub.domain.strategies.order_transition.mocks.OrderTaskMock;
import com.fiap.mechanical_hub.domain.strategies.order_transition.mocks.ServiceOrderMock;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.fiap.mechanical_hub.domain.strategies.order_transition.constants.TestConstants.DEFAULT_SERVICE_ID;
import static org.junit.jupiter.api.Assertions.*;

class FinishOrderTransitionTest {

    private final FinishOrderTransition transition = new FinishOrderTransition();

    @Test
    void shouldFinishOrderWhenAllTasksAreFinished() {
        ServiceOrder baseOrder = ServiceOrderMock.approvedOrder(false);
        List<OrderTask> tasks = List.of(
                OrderTaskMock.finishedTask(baseOrder.getId(), DEFAULT_SERVICE_ID)
        );
        ServiceOrder order = ServiceOrderMock.inExecutionOrderWithTasks(tasks);

        transition.execute(order);

        assertEquals(OrderStatusEnum.FINALIZADO, order.getStatus());
        assertNotNull(order.getCompletedAt());
    }

    @Test
    void shouldThrowExceptionWhenFinishingOrderWithPendingTasks() {
        ServiceOrder baseOrder = ServiceOrderMock.approvedOrder(false);
        List<OrderTask> tasks = List.of(OrderTaskMock.pendingTask(baseOrder.getId(), DEFAULT_SERVICE_ID));
        ServiceOrder order = ServiceOrderMock.inExecutionOrderWithTasks(tasks);

        InvalidOrderTransitionException exception = assertThrows(
                InvalidOrderTransitionException.class,
                () -> transition.execute(order)
        );

        assertTrue(exception.getMessage().contains("Não é possível finalizar"));
        assertEquals(OrderStatusEnum.EM_EXECUCAO, order.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenFinishingFromNonExecutionStatus() {
        ServiceOrder order = ServiceOrderMock.waitingApprovalOrder();

        InvalidOrderTransitionException exception = assertThrows(
                InvalidOrderTransitionException.class,
                () -> transition.execute(order)
        );

        assertTrue(exception.getMessage().contains("Invalid transition"));
        assertEquals(OrderStatusEnum.AGUARDANDO_APROVACAO, order.getStatus());
    }
}

