package com.fiap.mechanical_hub.domain.strategies.order_transition;

import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;
import com.fiap.mechanical_hub.domain.exceptions.InvalidOrderTransitionException;
import com.fiap.mechanical_hub.domain.strategies.order_transition.mocks.ServiceOrderMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApproveOrderTransitionTest {

    private final ApproveOrderTransition transition = new ApproveOrderTransition();

    @Test
    void shouldApproveOrderWhenStatusIsWaitingApproval() {
        ServiceOrder order = ServiceOrderMock.waitingApprovalOrder();

        transition.execute(order);

        assertEquals(OrderStatusEnum.APROVADO, order.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenApprovingFromNonWaitingApprovalStatus() {
        ServiceOrder order = ServiceOrderMock.inDiagnosisOrderWithBudget(
                com.fiap.mechanical_hub.domain.strategies.order_transition.constants.TestConstants.DEFAULT_BUDGET
        );

        InvalidOrderTransitionException exception = assertThrows(
                InvalidOrderTransitionException.class,
                () -> transition.execute(order)
        );

        assertTrue(exception.getMessage().contains("Invalid transition"));
        assertEquals(OrderStatusEnum.EM_DIAGNOSTICO, order.getStatus());
    }
}

