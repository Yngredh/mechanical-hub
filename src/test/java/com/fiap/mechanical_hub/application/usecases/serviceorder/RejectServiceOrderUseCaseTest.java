package com.fiap.mechanical_hub.application.usecases.serviceorder;

import com.fiap.mechanical_hub.application.command.serviceorder.RejectServiceOrderCommand;
import com.fiap.mechanical_hub.application.usecases.stock.RestoreReservedStockItemsUseCase;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.ServiceOrderRepository;
import com.fiap.mechanical_hub.domain.strategies.order_transition.OrderStatusTransition;
import com.fiap.mechanical_hub.domain.strategies.order_transition.OrderStatusTransitionFactory;
import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;
import com.fiap.mechanical_hub.mocks.domain.entities.ServiceOrderMock;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RejectServiceOrderUseCaseTest {

    private static final UUID ORDER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private final ServiceOrderRepository repository = mock(ServiceOrderRepository.class);
    private final OrderStatusTransitionFactory factory = mock(OrderStatusTransitionFactory.class);
    private final RestoreReservedStockItemsUseCase restoreReservedStockItemsUseCase = mock(RestoreReservedStockItemsUseCase.class);
    private final RejectServiceOrderUseCase useCase = new RejectServiceOrderUseCase(repository, factory, restoreReservedStockItemsUseCase);

    @Test
    void shouldRejectAndSaveOrder_whenOrderExists() {
        OrderStatusTransition transition = mock(OrderStatusTransition.class);
        when(repository.findById(ORDER_ID)).thenReturn(Optional.of(ServiceOrderMock.waitingApproval()));
        when(factory.get(OrderStatusEnum.RECUSADO)).thenReturn(transition);

        useCase.execute(new RejectServiceOrderCommand(ORDER_ID));

        verify(transition).execute(any());
        verify(repository).save(any());
    }

    @Test
    void shouldRestoreReservedStock_whenOrderIsRejected() {
        OrderStatusTransition transition = mock(OrderStatusTransition.class);
        when(repository.findById(ORDER_ID)).thenReturn(Optional.of(ServiceOrderMock.waitingApproval()));
        when(factory.get(OrderStatusEnum.RECUSADO)).thenReturn(transition);

        useCase.execute(new RejectServiceOrderCommand(ORDER_ID));

        verify(restoreReservedStockItemsUseCase).execute(any(), any());
    }

    @Test
    void shouldThrowNotFoundException_whenOrderDoesNotExist() {
        when(repository.findById(ORDER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new RejectServiceOrderCommand(ORDER_ID)))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(ORDER_ID.toString());
    }
}
