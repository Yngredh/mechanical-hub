package com.fiap.mechanical_hub.domain.strategies.order_transition;

import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class OrderStatusTransitionFactoryTest {

    private final OrderStatusTransition startDiagnosis = mock(OrderStatusTransition.class);
    private final OrderStatusTransition approve = mock(OrderStatusTransition.class);

    private final OrderStatusTransitionFactory factory = new OrderStatusTransitionFactory(
            Map.of(
                    OrderStatusEnum.EM_DIAGNOSTICO, startDiagnosis,
                    OrderStatusEnum.APROVADO, approve
            )
    );

    @Test
    void shouldReturnTransition_whenStatusIsRegistered() {
        OrderStatusTransition result = factory.get(OrderStatusEnum.EM_DIAGNOSTICO);

        assertThat(result).isEqualTo(startDiagnosis);
    }

    @Test
    void shouldThrowException_whenStatusIsNotRegistered() {
        assertThatThrownBy(() -> factory.get(OrderStatusEnum.ENTREGUE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported transition");
    }
}
