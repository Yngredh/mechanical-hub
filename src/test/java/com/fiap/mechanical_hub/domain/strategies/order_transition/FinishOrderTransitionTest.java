package com.fiap.mechanical_hub.domain.strategies.order_transition;

import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;
import com.fiap.mechanical_hub.domain.exceptions.InvalidOrderTransitionException;
import com.fiap.mechanical_hub.mocks.domain.entities.ServiceOrderMock;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FinishOrderTransitionTest {

    private final FinishOrderTransition transition = new FinishOrderTransition();

    @Test
    void shouldTransitionToFinished_whenAllTasksAreFinished() {
        ServiceOrder order = ServiceOrderMock.withAllTasksFinished();

        transition.execute(order);

        assertThat(order.getStatus()).isEqualTo(OrderStatusEnum.FINALIZADO);
    }

    @Test
    void shouldThrowException_whenOrderHasUnfinishedTasks() {
        ServiceOrder order = ServiceOrderMock.withOneUnfinishedTask();

        assertThatThrownBy(() -> transition.execute(order))
                .isInstanceOf(InvalidOrderTransitionException.class)
                .hasMessageContaining("não finalizados");
    }

    @Test
    void shouldThrowException_whenOrderIsNotInProgress() {
        ServiceOrder order = ServiceOrderMock.approvedWithoutStockPending();

        assertThatThrownBy(() -> transition.execute(order))
                .isInstanceOf(InvalidOrderTransitionException.class)
                .hasMessageContaining("Invalid transition");
    }
}
