package com.fiap.mechanical_hub.infrastructure.database.repositories.adapter;

import com.fiap.mechanical_hub.domain.entities.OrderTask;
import com.fiap.mechanical_hub.domain.enums.TaskStatusEnum;
import com.fiap.mechanical_hub.infrastructure.database.models.OrderTaskModel;
import com.fiap.mechanical_hub.infrastructure.database.models.ServiceModel;
import com.fiap.mechanical_hub.infrastructure.database.models.ServiceOrderModel;
import com.fiap.mechanical_hub.infrastructure.database.repositories.OrderTaskJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderTaskRepositoryAdapter")
class OrderTaskRepositoryAdapterTest {

    @Mock
    private OrderTaskJpaRepository jpaRepository;

    @InjectMocks
    private OrderTaskRepositoryAdapter repositoryAdapter;

    private UUID orderTaskId;
    private UUID serviceOrderId;
    private OrderTaskModel orderTaskModel;

    @BeforeEach
    void setUp() {
        orderTaskId = UUID.randomUUID();
        serviceOrderId = UUID.randomUUID();

        ServiceModel serviceModel = Mockito.mock(ServiceModel.class);
        ServiceOrderModel serviceOrderModel = Mockito.mock(ServiceOrderModel.class);

        orderTaskModel = new OrderTaskModel(
                orderTaskId,
                serviceOrderModel,
                serviceModel,
                TaskStatusEnum.PENDENTE.toString(),
                null,
                null
        );
    }

    @Test
    @DisplayName("findById should return mapped domain when found")
    void findByIdShouldReturnMappedDomain() {
        when(jpaRepository.findById(orderTaskId)).thenReturn(Optional.of(orderTaskModel));

        Optional<OrderTask> result = repositoryAdapter.findById(orderTaskId);

        assertThat(result).isPresent();
        verify(jpaRepository).findById(orderTaskId);
    }

    @Test
    @DisplayName("findById should return empty when not found")
    void findByIdShouldReturnEmptyWhenNotFound() {
        when(jpaRepository.findById(orderTaskId)).thenReturn(Optional.empty());

        Optional<OrderTask> result = repositoryAdapter.findById(orderTaskId);

        assertThat(result).isEmpty();
        verify(jpaRepository).findById(orderTaskId);
    }

    @Test
    @DisplayName("findAll should map all entities")
    void findAllShouldMapAllEntities() {
        when(jpaRepository.findAll()).thenReturn(List.of(orderTaskModel));

        List<OrderTask> result = repositoryAdapter.findAll();

        assertThat(result).hasSize(1);
        verify(jpaRepository).findAll();
    }

    @Test
    @DisplayName("findByServiceOrderId should map filtered entities")
    void findByServiceOrderIdShouldMapEntities() {
        when(jpaRepository.findByServiceOrderId(serviceOrderId))
                .thenReturn(List.of(orderTaskModel));

        List<OrderTask> result = repositoryAdapter.findByServiceOrderId(serviceOrderId);

        assertThat(result).hasSize(1);
        verify(jpaRepository).findByServiceOrderId(serviceOrderId);
    }


    @Test
    @DisplayName("deleteById should delegate to JPA repository")
    void deleteByIdShouldDelegateToJpaRepository() {
        repositoryAdapter.deleteById(orderTaskId);

        verify(jpaRepository).deleteById(orderTaskId);
    }

    @Test
    @DisplayName("findAverageExecutionTimeByService should return JPA result")
    void findAverageExecutionTimeByServiceShouldReturnJpaResult() {
        List<Object[]> mockResult = Arrays.asList(
                new Object[]{"Troca de óleo", 120.0},
                new Object[]{"Alinhamento", 80.0}
        );

        when(jpaRepository.findAverageExecutionTimeByService())
                .thenReturn(mockResult);

        List<Object[]> result = repositoryAdapter.findAverageExecutionTimeByService();

        assertThat(result).isEqualTo(mockResult);
        verify(jpaRepository).findAverageExecutionTimeByService();
    }
}