package com.fiap.mechanical_hub.domain.strategies.order_transition;

import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;
import com.fiap.mechanical_hub.domain.exceptions.InvalidOrderTransitionException;
import com.fiap.mechanical_hub.mocks.domain.entities.ServiceOrderMock;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeliverOrderTransitionTest {

    private final DeliverOrderTransition transition = new DeliverOrderTransition();

    @Test
    void shouldTransitionToDelivered_whenOrderIsFinished() {
        ServiceOrder order = ServiceOrderMock.finished();

        transition.execute(order);

        assertThat(order.getStatus()).isEqualTo(OrderStatusEnum.ENTREGUE);
    }

    @Test
    void shouldThrowException_whenOrderIsNotFinished() {
        ServiceOrder order = ServiceOrderMock.inProgress();

        assertThatThrownBy(() -> transition.execute(order))
                .isInstanceOf(InvalidOrderTransitionException.class)
                .hasMessageContaining("Invalid transition");
    }
}
