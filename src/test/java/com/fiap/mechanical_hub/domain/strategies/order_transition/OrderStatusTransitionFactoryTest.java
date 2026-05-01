package com.fiap.mechanical_hub.domain.strategies.order_transition;

import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OrderStatusTransitionFactoryTest {

    @Test
    void shouldReturnRegisteredTransitionForTargetStatus() {
        StartDiagnosisTransition startDiagnosisTransition = new StartDiagnosisTransition();
        OrderStatusTransitionFactory factory = new OrderStatusTransitionFactory(
                Map.of(OrderStatusEnum.EM_DIAGNOSTICO, startDiagnosisTransition)
        );

        OrderStatusTransition transition = factory.get(OrderStatusEnum.EM_DIAGNOSTICO);

        assertSame(startDiagnosisTransition, transition);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenTransitionIsNotRegistered() {
        OrderStatusTransitionFactory factory = new OrderStatusTransitionFactory(Map.of());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> factory.get(OrderStatusEnum.EM_DIAGNOSTICO)
        );

        assertEquals("Unsupported transition", exception.getMessage());
    }

}

