package com.fiap.mechanical_hub.application.usecases.serviceorder;

import com.fiap.mechanical_hub.application.command.serviceorder.ApproveServiceOrderCommand;
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

class ApproveServiceOrderUseCaseTest {

    private static final UUID ORDER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private final ServiceOrderRepository repository = mock(ServiceOrderRepository.class);
    private final OrderStatusTransitionFactory factory = mock(OrderStatusTransitionFactory.class);
    private final ApproveServiceOrderUseCase useCase = new ApproveServiceOrderUseCase(repository, factory);

    @Test
    void shouldApproveAndSaveOrder_whenOrderExists() {
        OrderStatusTransition transition = mock(OrderStatusTransition.class);
        when(repository.findById(ORDER_ID)).thenReturn(Optional.of(ServiceOrderMock.waitingApproval()));
        when(factory.get(OrderStatusEnum.APROVADO)).thenReturn(transition);

        useCase.execute(new ApproveServiceOrderCommand(ORDER_ID));

        verify(transition).execute(any());
        verify(repository).save(any());
    }

    @Test
    void shouldThrowNotFoundException_whenOrderDoesNotExist() {
        when(repository.findById(ORDER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new ApproveServiceOrderCommand(ORDER_ID)))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(ORDER_ID.toString());
    }
}
