package com.fiap.mechanical_hub.application.usecases.serviceorder;

import com.fiap.mechanical_hub.application.command.serviceorder.UpdateServiceOrderStatusCommand;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.ServiceOrderRepository;
import com.fiap.mechanical_hub.domain.strategies.order_transition.OrderStatusTransition;
import com.fiap.mechanical_hub.domain.strategies.order_transition.OrderStatusTransitionFactory;
import com.fiap.mechanical_hub.mocks.domain.entities.ServiceOrderMock;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpdateServiceOrderStatusUseCaseTest {

    private static final UUID ORDER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000004");

    private final ServiceOrderRepository repository = mock(ServiceOrderRepository.class);
    private final OrderStatusTransitionFactory factory = mock(OrderStatusTransitionFactory.class);
    private final UpdateServiceOrderStatusUseCase useCase = new UpdateServiceOrderStatusUseCase(repository, factory);

    @Test
    void shouldApplyTransitionAndReturnSavedOrder_whenOrderExists() {
        ServiceOrder order = ServiceOrderMock.inDiagnosis();
        OrderStatusTransition transition = mock(OrderStatusTransition.class);
        when(repository.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(factory.get(OrderStatusEnum.AGUARDANDO_APROVACAO)).thenReturn(transition);
        when(repository.save(any())).thenReturn(order);

        ServiceOrder result = useCase.execute(new UpdateServiceOrderStatusCommand(ORDER_ID, OrderStatusEnum.AGUARDANDO_APROVACAO, USER_ID));

        assertThat(result).isNotNull();
        verify(transition).execute(order);
        verify(repository).save(order);
    }

    @Test
    void shouldThrowNotFoundException_whenOrderDoesNotExist() {
        when(repository.findById(ORDER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new UpdateServiceOrderStatusCommand(ORDER_ID, OrderStatusEnum.APROVADO, USER_ID)))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(ORDER_ID.toString());
    }
}
