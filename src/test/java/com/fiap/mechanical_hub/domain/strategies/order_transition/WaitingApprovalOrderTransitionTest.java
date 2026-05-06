package com.fiap.mechanical_hub.domain.strategies.order_transition;

import com.fiap.mechanical_hub.application.interfaces.SendBudgetApproval;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;
import com.fiap.mechanical_hub.domain.exceptions.BusinessRuleException;
import com.fiap.mechanical_hub.domain.exceptions.InvalidOrderTransitionException;
import com.fiap.mechanical_hub.domain.strategies.order_transition.mocks.ServiceOrderMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static com.fiap.mechanical_hub.domain.strategies.order_transition.constants.TestConstants.DEFAULT_BUDGET;
import static com.fiap.mechanical_hub.domain.strategies.order_transition.constants.TestConstants.ZERO_BUDGET;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WaitingApprovalOrderTransitionTest {

    @Mock
    private SendBudgetApproval sendBudgetApproval;

    @InjectMocks
    private WaitingApprovalOrderTransition transition;

    private final UUID userId = UUID.randomUUID();

    @Test
    void shouldSubmitForApprovalAndSendBudgetApprovalRequestWhenBudgetIsGenerated() {
        ServiceOrder order = ServiceOrderMock.inDiagnosisOrderWithBudget(DEFAULT_BUDGET);

        transition.execute(order, userId);

        assertEquals(OrderStatusEnum.AGUARDANDO_APROVACAO, order.getStatus());
        verify(sendBudgetApproval, times(1)).sendBudgetApprovalRequest(order);
        verifyNoMoreInteractions(sendBudgetApproval);
    }

    @Test
    void shouldThrowBusinessRuleExceptionAndNotSendRequestWhenBudgetIsNull() {
        ServiceOrder order = ServiceOrderMock.inDiagnosisOrderWithoutBudget();

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> transition.execute(order, userId)
        );

        assertEquals("Order budget not generated", exception.getMessage());
        assertEquals(OrderStatusEnum.EM_DIAGNOSTICO, order.getStatus());
        verifyNoInteractions(sendBudgetApproval);
    }

    @Test
    void shouldThrowBusinessRuleExceptionAndNotSendRequestWhenBudgetIsZero() {
        ServiceOrder order = ServiceOrderMock.inDiagnosisOrderWithBudget(ZERO_BUDGET);

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> transition.execute(order, userId)
        );

        assertEquals("Order budget not generated", exception.getMessage());
        assertEquals(OrderStatusEnum.EM_DIAGNOSTICO, order.getStatus());
        verifyNoInteractions(sendBudgetApproval);
    }

    @Test
    void shouldThrowInvalidOrderTransitionExceptionAndNotSendRequestWhenStatusIsNotInDiagnosis() {
        ServiceOrder order = ServiceOrderMock.receivedOrder();

        InvalidOrderTransitionException exception = assertThrows(
                InvalidOrderTransitionException.class,
                () -> transition.execute(order, userId)
        );

        assertTrue(exception.getMessage().contains("Invalid transition"));
        assertEquals(OrderStatusEnum.RECEBIDO, order.getStatus());
        verifyNoInteractions(sendBudgetApproval);
    }
}

