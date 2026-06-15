package com.fiap.mechanical_hub.infrastructure.database.adapter;

import com.fiap.mechanical_hub.domain.entities.OrderTask;
import com.fiap.mechanical_hub.mocks.infrastructure.database.models.OrderTaskModelMock;
import com.fiap.mechanical_hub.mocks.infrastructure.database.models.ServiceModelMock;
import com.fiap.mechanical_hub.infrastructure.database.repositories.OrderTaskJpaRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrderTaskRepositoryAdapterTest {

    private final OrderTaskJpaRepository jpaRepository = mock(OrderTaskJpaRepository.class);

    private final OrderTaskRepositoryAdapter adapter = new OrderTaskRepositoryAdapter(jpaRepository);

    @Test
    void shouldReturnOrderTask_whenFindByIdAndTaskExists() {
        when(jpaRepository.findById(OrderTaskModelMock.TASK_ID))
                .thenReturn(Optional.of(OrderTaskModelMock.notStarted()));

        Optional<OrderTask> result = adapter.findById(OrderTaskModelMock.TASK_ID);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(OrderTaskModelMock.TASK_ID);
    }

    @Test
    void shouldReturnEmpty_whenFindByIdAndTaskDoesNotExist() {
        when(jpaRepository.findById(OrderTaskModelMock.TASK_ID)).thenReturn(Optional.empty());

        Optional<OrderTask> result = adapter.findById(OrderTaskModelMock.TASK_ID);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnAllTasks_whenFindAll() {
        when(jpaRepository.findAll())
                .thenReturn(List.of(OrderTaskModelMock.notStarted(), OrderTaskModelMock.finished()));

        List<OrderTask> result = adapter.findAll();

        assertThat(result).hasSize(2);
    }

    @Test
    void shouldReturnTasksByServiceId_whenFindAllByServiceId() {
        when(jpaRepository.findByServiceId(ServiceModelMock.SERVICE_ID))
                .thenReturn(List.of(OrderTaskModelMock.notStarted()));

        List<OrderTask> result = adapter.findAllByServiceId(ServiceModelMock.SERVICE_ID);

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldReturnEmptyList_whenFindAllByServiceIdAndNoTasksExist() {
        when(jpaRepository.findByServiceId(ServiceModelMock.SERVICE_ID)).thenReturn(List.of());

        List<OrderTask> result = adapter.findAllByServiceId(ServiceModelMock.SERVICE_ID);

        assertThat(result).isEmpty();
    }
}
