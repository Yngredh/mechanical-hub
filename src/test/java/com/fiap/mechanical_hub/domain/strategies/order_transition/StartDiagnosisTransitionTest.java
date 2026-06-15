package com.fiap.mechanical_hub.domain.strategies.order_transition;

import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;
import com.fiap.mechanical_hub.domain.exceptions.InvalidOrderTransitionException;
import com.fiap.mechanical_hub.mocks.domain.entities.ServiceOrderMock;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StartDiagnosisTransitionTest {

    private final StartDiagnosisTransition transition = new StartDiagnosisTransition();

    @Test
    void shouldTransitionToInDiagnosis_whenOrderIsReceived() {
        ServiceOrder order = ServiceOrderMock.received();

        transition.execute(order);

        assertThat(order.getStatus()).isEqualTo(OrderStatusEnum.EM_DIAGNOSTICO);
    }

    @Test
    void shouldThrowException_whenOrderIsNotReceived() {
        ServiceOrder order = ServiceOrderMock.inDiagnosis();

        assertThatThrownBy(() -> transition.execute(order))
                .isInstanceOf(InvalidOrderTransitionException.class)
                .hasMessageContaining("Invalid transition");
    }
}
