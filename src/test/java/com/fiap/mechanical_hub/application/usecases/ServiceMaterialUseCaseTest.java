package com.fiap.mechanical_hub.application.usecases;

import com.fiap.mechanical_hub.application.repositories.ServiceMaterialRepository;
import com.fiap.mechanical_hub.domain.entities.Material;
import com.fiap.mechanical_hub.domain.entities.ServiceMaterial;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceMaterialUseCaseTest {

    @Mock
    private ServiceMaterialRepository serviceMaterialRepository;

    @InjectMocks
    private ServiceMaterialUseCase serviceMaterialUseCase;

    @Test
    @DisplayName("Deve retornar uma lista de materiais de serviço para um determinado ServiceId")
    void shouldReturnServiceMaterialsWhenServiceIdExists() {
        UUID serviceId = UUID.randomUUID();
        Material material = new Material(UUID.randomUUID(), "Óleo 5W30","Oleo", new BigDecimal("50.00"), 10, null, null);

        ServiceMaterial item1 = new ServiceMaterial(UUID.randomUUID(), serviceId, material, 4);
        ServiceMaterial item2 = new ServiceMaterial(UUID.randomUUID(), serviceId, material, 1);

        List<ServiceMaterial> expectedList = List.of(item1, item2);

        when(serviceMaterialRepository.findByServiceId(serviceId)).thenReturn(expectedList);

        List<ServiceMaterial> result = serviceMaterialUseCase.getServiceMaterials(serviceId);

        assertThat(result)
                .isNotNull()
                .hasSize(2)
                .containsExactly(item1, item2);

        verify(serviceMaterialRepository, times(1)).findByServiceId(serviceId);
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando não houver materiais para o ServiceId")
    void shouldReturnEmptyListWhenNoMaterialsFound() {
        UUID serviceId = UUID.randomUUID();
        when(serviceMaterialRepository.findByServiceId(serviceId)).thenReturn(List.of());

        List<ServiceMaterial> result = serviceMaterialUseCase.getServiceMaterials(serviceId);

        assertThat(result).isEmpty();
        verify(serviceMaterialRepository, times(1)).findByServiceId(serviceId);
    }
}