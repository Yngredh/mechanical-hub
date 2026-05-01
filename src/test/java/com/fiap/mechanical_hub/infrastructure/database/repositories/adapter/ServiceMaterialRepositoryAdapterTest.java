package com.fiap.mechanical_hub.infrastructure.database.repositories.adapter;

import com.fiap.mechanical_hub.domain.entities.ServiceData;
import com.fiap.mechanical_hub.domain.entities.ServiceMaterial;
import com.fiap.mechanical_hub.infrastructure.database.models.ServiceMaterialModel;
import com.fiap.mechanical_hub.infrastructure.database.repositories.ServiceMaterialJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static com.fiap.mechanical_hub.application.mappers.ServiceMapper.toJpaEntity;
import static com.fiap.mechanical_hub.application.mappers.ServiceMaterialMapper.toDomainEntity;
import static com.fiap.mechanical_hub.application.mappers.ServiceMaterialMapper.toJpaEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ServiceMaterialRepositoryAdapter")
class ServiceMaterialRepositoryAdapterTest {

    @Mock
    private ServiceMaterialJpaRepository jpaRepository;

    @InjectMocks
    private ServiceMaterialRepositoryAdapter repositoryAdapter;

    private UUID serviceMaterialId;
    private UUID serviceId;

    private ServiceMaterial serviceMaterial;
    private ServiceData service;

    private ServiceMaterialModel serviceMaterialModel;

    @BeforeEach
    void setUp() {
        serviceMaterialId = UUID.randomUUID();
        serviceId = UUID.randomUUID();

        serviceMaterial = Mockito.mock(ServiceMaterial.class);
        service = Mockito.mock(ServiceData.class);

        serviceMaterialModel = Mockito.mock(ServiceMaterialModel.class);
    }

    @Test
    @DisplayName("save should map and persist entity")
    void saveShouldMapAndPersistEntity() {

        try (MockedStatic<com.fiap.mechanical_hub.application.mappers.ServiceMaterialMapper> materialMapperMock =
                     Mockito.mockStatic(com.fiap.mechanical_hub.application.mappers.ServiceMaterialMapper.class);
             MockedStatic<com.fiap.mechanical_hub.application.mappers.ServiceMapper> serviceMapperMock =
                     Mockito.mockStatic(com.fiap.mechanical_hub.application.mappers.ServiceMapper.class)) {

            var serviceModel = Mockito.mock(com.fiap.mechanical_hub.infrastructure.database.models.ServiceModel.class);

            serviceMapperMock.when(() -> toJpaEntity(service)).thenReturn(serviceModel);
            materialMapperMock.when(() -> toJpaEntity(serviceMaterial, serviceModel))
                    .thenReturn(serviceMaterialModel);
            materialMapperMock.when(() -> toDomainEntity(serviceMaterialModel))
                    .thenReturn(serviceMaterial);

            when(jpaRepository.save(serviceMaterialModel)).thenReturn(serviceMaterialModel);

            ServiceMaterial result = repositoryAdapter.save(serviceMaterial, service);

            assertThat(result).isEqualTo(serviceMaterial);
            verify(jpaRepository).save(serviceMaterialModel);
        }
    }

    @Test
    @DisplayName("findByServiceId should return mapped list")
    void findByServiceIdShouldReturnMappedList() {

        try (MockedStatic<com.fiap.mechanical_hub.application.mappers.ServiceMaterialMapper> materialMapperMock =
                     Mockito.mockStatic(com.fiap.mechanical_hub.application.mappers.ServiceMaterialMapper.class)) {

            materialMapperMock.when(() -> toDomainEntity(serviceMaterialModel))
                    .thenReturn(serviceMaterial);

            when(jpaRepository.findByServiceId(serviceId))
                    .thenReturn(List.of(serviceMaterialModel));

            List<ServiceMaterial> result = repositoryAdapter.findByServiceId(serviceId);

            assertThat(result).hasSize(1);
            verify(jpaRepository).findByServiceId(serviceId);
        }
    }

    @Test
    @DisplayName("findByServiceId should return empty list when no data")
    void findByServiceIdShouldReturnEmptyList() {

        when(jpaRepository.findByServiceId(serviceId))
                .thenReturn(List.of());

        List<ServiceMaterial> result = repositoryAdapter.findByServiceId(serviceId);

        assertThat(result).isEmpty();
        verify(jpaRepository).findByServiceId(serviceId);
    }

    @Test
    @DisplayName("deleteById should delegate to JPA repository")
    void deleteByIdShouldDelegateToJpaRepository() {

        repositoryAdapter.deleteById(serviceMaterialId);

        verify(jpaRepository).deleteById(serviceMaterialId);
    }
}