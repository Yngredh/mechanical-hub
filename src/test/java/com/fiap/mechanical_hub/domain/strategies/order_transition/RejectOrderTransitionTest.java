package com.fiap.mechanical_hub.domain.strategies.order_transition;

import com.fiap.mechanical_hub.application.usecases.StockUseCase;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;
import com.fiap.mechanical_hub.domain.exceptions.InvalidOrderTransitionException;
import com.fiap.mechanical_hub.domain.strategies.order_transition.mocks.ServiceOrderMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RejectOrderTransitionTest {

    @Mock
    private StockUseCase stockUseCase;

    @InjectMocks
    private RejectOrderTransition transition;

    @Test
    void shouldRejectOrderAndRestoreReservedItemsWhenStatusIsWaitingApproval() {
        ServiceOrder order = ServiceOrderMock.waitingApprovalOrder();

        transition.execute(order);

        assertEquals(OrderStatusEnum.RECUSADO, order.getStatus());
        verify(stockUseCase, times(1)).restoreReservedItems(order);
        verifyNoMoreInteractions(stockUseCase);
    }

    @Test
    void shouldThrowExceptionAndNotRestoreReservedItemsWhenStatusIsNotWaitingApproval() {
        ServiceOrder order = ServiceOrderMock.inDiagnosisOrderWithBudget(
                com.fiap.mechanical_hub.domain.strategies.order_transition.constants.TestConstants.DEFAULT_BUDGET
        );

        InvalidOrderTransitionException exception = assertThrows(
                InvalidOrderTransitionException.class,
                () -> transition.execute(order)
        );

        assertTrue(exception.getMessage().contains("Invalid transition"));
        assertEquals(OrderStatusEnum.EM_DIAGNOSTICO, order.getStatus());
        verifyNoInteractions(stockUseCase);
    }
}

