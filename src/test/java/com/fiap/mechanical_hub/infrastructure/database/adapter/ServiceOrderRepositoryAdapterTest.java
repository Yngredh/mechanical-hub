package com.fiap.mechanical_hub.infrastructure.database.adapter;

import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.mocks.domain.entities.ServiceOrderMock;
import com.fiap.mechanical_hub.mocks.infrastructure.database.models.ServiceOrderModelMock;
import com.fiap.mechanical_hub.infrastructure.database.repositories.ServiceOrderJpaRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ServiceOrderRepositoryAdapterTest {

    private final ServiceOrderJpaRepository jpaRepository = mock(ServiceOrderJpaRepository.class);

    private final ServiceOrderRepositoryAdapter adapter = new ServiceOrderRepositoryAdapter(jpaRepository);

    @Test
    void shouldReturnSavedServiceOrder_whenSavingServiceOrder() {
        when(jpaRepository.save(any())).thenReturn(ServiceOrderModelMock.received());

        ServiceOrder result = adapter.save(ServiceOrderMock.received());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(ServiceOrderModelMock.ORDER_ID);
    }

    @Test
    void shouldDelegateToJpaRepository_whenSavingServiceOrder() {
        when(jpaRepository.save(any())).thenReturn(ServiceOrderModelMock.received());

        adapter.save(ServiceOrderMock.received());

        verify(jpaRepository).save(any());
    }

    @Test
    void shouldReturnServiceOrder_whenFindByIdAndOrderExists() {
        when(jpaRepository.findById(ServiceOrderModelMock.ORDER_ID))
                .thenReturn(Optional.of(ServiceOrderModelMock.received()));

        Optional<ServiceOrder> result = adapter.findById(ServiceOrderModelMock.ORDER_ID);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(ServiceOrderModelMock.ORDER_ID);
    }

    @Test
    void shouldReturnEmpty_whenFindByIdAndOrderDoesNotExist() {
        when(jpaRepository.findById(ServiceOrderModelMock.ORDER_ID)).thenReturn(Optional.empty());

        Optional<ServiceOrder> result = adapter.findById(ServiceOrderModelMock.ORDER_ID);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnAllOrders_whenFindAll() {
        when(jpaRepository.findAll()).thenReturn(List.of(ServiceOrderModelMock.received()));

        List<ServiceOrder> result = adapter.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(ServiceOrderModelMock.ORDER_ID);
    }

    @Test
    void shouldReturnOrderNumber_whenFindLastOrderNumberByYearMonthAndExists() {
        when(jpaRepository.findLastOrderNumberByYearMonth("2024-01"))
                .thenReturn(Optional.of("OS-202401-0001"));

        Optional<String> result = adapter.findLastOrderNumberByYearMonth("2024-01");

        assertThat(result).isPresent().contains("OS-202401-0001");
    }

    @Test
    void shouldReturnEmpty_whenFindLastOrderNumberByYearMonthAndNotExists() {
        when(jpaRepository.findLastOrderNumberByYearMonth("2024-02")).thenReturn(Optional.empty());

        Optional<String> result = adapter.findLastOrderNumberByYearMonth("2024-02");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnOrder_whenFindByOrderNumberAndExists() {
        when(jpaRepository.findByOrderNumber("OS-202401-0001"))
                .thenReturn(Optional.of(ServiceOrderModelMock.received()));

        Optional<ServiceOrder> result = adapter.findByOrderNumber("OS-202401-0001");

        assertThat(result).isPresent();
        assertThat(result.get().getOrderNumber()).isEqualTo("OS-202401-0001");
    }

    @Test
    void shouldReturnOrdersOrderedByCreatedAt_whenFindAllByOrderByCreatedAtDesc() {
        when(jpaRepository.findAllByOrderByCreatedAtDesc())
                .thenReturn(List.of(ServiceOrderModelMock.received()));

        List<ServiceOrder> result = adapter.findAllByOrderByCreatedAtDesc();

        assertThat(result).hasSize(1);
    }
}
