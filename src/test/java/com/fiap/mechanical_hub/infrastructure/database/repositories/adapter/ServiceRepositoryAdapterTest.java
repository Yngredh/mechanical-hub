package com.fiap.mechanical_hub.infrastructure.database.repositories.adapter;

import com.fiap.mechanical_hub.domain.entities.Service;
import com.fiap.mechanical_hub.infrastructure.database.models.ServiceModel;
import com.fiap.mechanical_hub.infrastructure.database.repositories.ServiceJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ServiceRepositoryAdapter")
class ServiceRepositoryAdapterTest {

    @Mock
    private ServiceJpaRepository jpaRepository;

    @InjectMocks
    private ServiceRepositoryAdapter repositoryAdapter;

    private UUID serviceId;
    private Service service;
    private ServiceModel serviceModel;

    @BeforeEach
    void setUp() {
        serviceId = UUID.randomUUID();

        service = Mockito.mock(Service.class);
        serviceModel = Mockito.mock(ServiceModel.class);
    }

    @Test
    @DisplayName("save should persist and return mapped domain entity")
    void saveShouldPersistAndReturnMappedDomain() {
        when(jpaRepository.save(Mockito.any(ServiceModel.class)))
                .thenReturn(serviceModel);

        Service result = repositoryAdapter.save(service);

        assertThat(result).isNotNull();
        verify(jpaRepository).save(Mockito.any(ServiceModel.class));
    }

    @Test
    @DisplayName("findById should return mapped domain when found")
    void findByIdShouldReturnMappedDomain() {
        when(jpaRepository.findById(serviceId))
                .thenReturn(Optional.of(serviceModel));

        Optional<Service> result = repositoryAdapter.findById(serviceId);

        assertThat(result).isPresent();
        verify(jpaRepository).findById(serviceId);
    }

    @Test
    @DisplayName("findById should return empty when not found")
    void findByIdShouldReturnEmptyWhenNotFound() {
        when(jpaRepository.findById(serviceId))
                .thenReturn(Optional.empty());

        Optional<Service> result = repositoryAdapter.findById(serviceId);

        assertThat(result).isEmpty();
        verify(jpaRepository).findById(serviceId);
    }

    @Test
    @DisplayName("findAll should map all entities")
    void findAllShouldMapAllEntities() {
        when(jpaRepository.findAll())
                .thenReturn(List.of(serviceModel));

        List<Service> result = repositoryAdapter.findAll();

        assertThat(result).hasSize(1);
        verify(jpaRepository).findAll();
    }

    @Test
    @DisplayName("deleteById should delegate to JPA repository")
    void deleteByIdShouldDelegateToJpaRepository() {
        repositoryAdapter.deleteById(serviceId);

        verify(jpaRepository).deleteById(serviceId);
    }

    @Test
    @DisplayName("findByIds should return mapped list")
    void findByIdsShouldReturnMappedList() {
        List<UUID> ids = List.of(serviceId);

        when(jpaRepository.findByIdIn(ids))
                .thenReturn(List.of(serviceModel));

        List<Service> result = repositoryAdapter.findByIds(ids);

        assertThat(result).hasSize(1);
        verify(jpaRepository).findByIdIn(ids);
    }
}