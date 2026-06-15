package com.fiap.mechanical_hub.domain.strategies.order_transition;

import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;
import com.fiap.mechanical_hub.domain.exceptions.InvalidOrderTransitionException;
import com.fiap.mechanical_hub.mocks.domain.entities.ServiceOrderMock;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RejectOrderTransitionTest {

    private final RejectOrderTransition transition = new RejectOrderTransition();

    @Test
    void shouldTransitionToRejected_whenOrderIsWaitingApproval() {
        ServiceOrder order = ServiceOrderMock.waitingApproval();

        transition.execute(order);

        assertThat(order.getStatus()).isEqualTo(OrderStatusEnum.RECUSADO);
    }

    @Test
    void shouldThrowException_whenOrderIsNotWaitingApproval() {
        ServiceOrder order = ServiceOrderMock.received();

        assertThatThrownBy(() -> transition.execute(order))
                .isInstanceOf(InvalidOrderTransitionException.class)
                .hasMessageContaining("Invalid transition");
    }
}
