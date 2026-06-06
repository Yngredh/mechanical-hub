package com.fiap.mechanical_hub.application.usecases.serviceorder;

import com.fiap.mechanical_hub.application.command.serviceorder.UpdateTaskStatusCommand;
import com.fiap.mechanical_hub.application.usecases.stock.RegisterStockOutUseCase;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.enums.TaskStatusEnum;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.ServiceOrderRepository;
import com.fiap.mechanical_hub.mocks.domain.entities.OrderTaskMock;
import com.fiap.mechanical_hub.mocks.domain.entities.ServiceOrderMock;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpdateTaskStatusUseCaseTest {

    private static final UUID ORDER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID TASK_ID = UUID.fromString("00000000-0000-0000-0000-000000000040");

    private final ServiceOrderRepository repository = mock(ServiceOrderRepository.class);
    private final RegisterStockOutUseCase registerStockOutUseCase = mock(RegisterStockOutUseCase.class);
    private final UpdateTaskStatusUseCase useCase = new UpdateTaskStatusUseCase(repository, registerStockOutUseCase);

    @Test
    void shouldStartTaskAndSave_whenStatusIsIniciado() {
        ServiceOrder order = ServiceOrderMock.approvedWithoutStockPending();
        order.addTask(OrderTaskMock.notStarted());
        when(repository.findById(ORDER_ID)).thenReturn(Optional.of(order));

        useCase.execute(new UpdateTaskStatusCommand(ORDER_ID, TASK_ID, TaskStatusEnum.INICIADO));

        verify(repository).save(order);
    }

    @Test
    void shouldRegisterStockOutAndSave_whenStatusIsFinalizado() {
        ServiceOrder order = ServiceOrderMock.inProgress();
        order.addTask(OrderTaskMock.started());
        when(repository.findById(ORDER_ID)).thenReturn(Optional.of(order));

        useCase.execute(new UpdateTaskStatusCommand(ORDER_ID, TASK_ID, TaskStatusEnum.FINALIZADO));

        verify(registerStockOutUseCase).execute(any(), any());
        verify(repository).save(order);
    }

    @Test
    void shouldThrowNotFoundException_whenOrderDoesNotExist() {
        when(repository.findById(ORDER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new UpdateTaskStatusCommand(ORDER_ID, TASK_ID, TaskStatusEnum.INICIADO)))
                .isInstanceOf(NotFoundException.class);
    }
}
