package com.fiap.mechanical_hub.infrastructure.database.repositories.adapter;

import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderSummaryResponse;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.infrastructure.database.models.ServiceOrderModel;
import com.fiap.mechanical_hub.infrastructure.database.repositories.ServiceOrderJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ServiceOrderRepositoryAdapter")
class ServiceOrderRepositoryAdapterTest {

    @Mock
    private ServiceOrderJpaRepository jpaRepository;

    @InjectMocks
    private ServiceOrderRepositoryAdapter repositoryAdapter;

    private UUID serviceOrderId;
    private UUID customerId;
    private ServiceOrderModel serviceOrderModel;
    private ServiceOrder serviceOrder;

    @BeforeEach
    void setUp() {
        serviceOrderId = UUID.randomUUID();
        customerId = UUID.randomUUID();

        serviceOrderModel = Mockito.mock(ServiceOrderModel.class);
        serviceOrder = Mockito.mock(ServiceOrder.class);
    }

    @Test
    @DisplayName("findAllSummaries should return JPA result")
    void findAllSummariesShouldReturnJpaResult() {
        List<ServiceOrderSummaryResponse> mockResponse = List.of(
                Mockito.mock(ServiceOrderSummaryResponse.class)
        );

        when(jpaRepository.findAllSummaries(
                Mockito.anyString(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any()
        )).thenReturn(mockResponse);

        List<ServiceOrderSummaryResponse> result = repositoryAdapter
                .findAllSummaries("OPEN", customerId, LocalDateTime.now(), LocalDateTime.now());

        assertThat(result).isEqualTo(mockResponse);
        verify(jpaRepository).findAllSummaries(
                Mockito.anyString(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any()
        );
    }

    @Test
    @DisplayName("findSummaryByCustomerId should map entities")
    void findSummaryByCustomerIdShouldMapEntities() {
        when(jpaRepository.findSummaryByCustomerId(customerId))
                .thenReturn(List.of(serviceOrderModel));

        List<ServiceOrder> result = repositoryAdapter.findSummaryByCustomerId(customerId);

        assertThat(result).hasSize(1);
        verify(jpaRepository).findSummaryByCustomerId(customerId);
    }

    @Test
    @DisplayName("save should map and persist entity")
    void saveShouldMapAndPersistEntity() {
        when(jpaRepository.save(Mockito.any(ServiceOrderModel.class)))
                .thenReturn(serviceOrderModel);

        ServiceOrder result = repositoryAdapter.save(serviceOrder);

        assertThat(result).isNotNull();
        verify(jpaRepository).save(Mockito.any(ServiceOrderModel.class));
    }

    @Test
    @DisplayName("findLastOrderNumberByYearMonth should return value")
    void findLastOrderNumberByYearMonthShouldReturnValue() {
        when(jpaRepository.findLastOrderNumberByYearMonth("2026-05"))
                .thenReturn(Optional.of("OS-001"));

        Optional<String> result = repositoryAdapter.findLastOrderNumberByYearMonth("2026-05");

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo("OS-001");
        verify(jpaRepository).findLastOrderNumberByYearMonth("2026-05");
    }

    @Test
    @DisplayName("findById should return mapped domain when found")
    void findByIdShouldReturnMappedDomain() {
        when(jpaRepository.findById(serviceOrderId))
                .thenReturn(Optional.of(serviceOrderModel));

        Optional<ServiceOrder> result = repositoryAdapter.findById(serviceOrderId);

        assertThat(result).isPresent();
        verify(jpaRepository).findById(serviceOrderId);
    }

    @Test
    @DisplayName("findById should return empty when not found")
    void findByIdShouldReturnEmptyWhenNotFound() {
        when(jpaRepository.findById(serviceOrderId))
                .thenReturn(Optional.empty());

        Optional<ServiceOrder> result = repositoryAdapter.findById(serviceOrderId);

        assertThat(result).isEmpty();
        verify(jpaRepository).findById(serviceOrderId);
    }

    @Test
    @DisplayName("findAll should map all entities")
    void findAllShouldMapAllEntities() {
        when(jpaRepository.findAll())
                .thenReturn(List.of(serviceOrderModel));

        List<ServiceOrder> result = repositoryAdapter.findAll();

        assertThat(result).hasSize(1);
        verify(jpaRepository).findAll();
    }

    @Test
    @DisplayName("deleteById should delegate to JPA repository")
    void deleteByIdShouldDelegateToJpaRepository() {
        repositoryAdapter.deleteById(serviceOrderId);

        verify(jpaRepository).deleteById(serviceOrderId);
    }

    @Test
    @DisplayName("findAllByOrderByCreatedAtDesc should map entities")
    void findAllByOrderByCreatedAtDescShouldMapEntities() {
        when(jpaRepository.findAllByOrderByCreatedAtDesc())
                .thenReturn(List.of(serviceOrderModel));

        List<ServiceOrder> result = repositoryAdapter.findAllByOrderByCreatedAtDesc();

        assertThat(result).hasSize(1);
        verify(jpaRepository).findAllByOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("findByOrderNumber should return mapped domain when found")
    void findByOrderNumberShouldReturnMappedDomain() {
        when(jpaRepository.findByOrderNumber("OS-001"))
                .thenReturn(Optional.of(serviceOrderModel));

        Optional<ServiceOrder> result = repositoryAdapter.findByOrderNumber("OS-001");

        assertThat(result).isPresent();
        verify(jpaRepository).findByOrderNumber("OS-001");
    }

    @Test
    @DisplayName("findByOrderNumber should return empty when not found")
    void findByOrderNumberShouldReturnEmptyWhenNotFound() {
        when(jpaRepository.findByOrderNumber("OS-001"))
                .thenReturn(Optional.empty());

        Optional<ServiceOrder> result = repositoryAdapter.findByOrderNumber("OS-001");

        assertThat(result).isEmpty();
        verify(jpaRepository).findByOrderNumber("OS-001");
    }
}