package com.fiap.mechanical_hub.application.usecases.service;

import com.fiap.mechanical_hub.application.command.ordertask.CreateServiceCommand;
import com.fiap.mechanical_hub.application.dto.service.ServiceResponse;
import com.fiap.mechanical_hub.application.dto.servicematerials.ServiceMaterialRequest;
import com.fiap.mechanical_hub.domain.entities.Material;
import com.fiap.mechanical_hub.domain.entities.ServiceData;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.MaterialRepository;
import com.fiap.mechanical_hub.domain.repositories.ServiceRepository;
import com.fiap.mechanical_hub.mocks.domain.entities.MaterialMock;
import com.fiap.mechanical_hub.mocks.domain.entities.ServiceDataMock;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateServiceUseCaseTest {

    private static final UUID MATERIAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000020");

    private final MaterialRepository materialRepository = mock(MaterialRepository.class);
    private final ServiceRepository serviceRepository = mock(ServiceRepository.class);
    private final CreateServiceUseCase useCase = new CreateServiceUseCase(materialRepository, serviceRepository);

    @Test
    void shouldCreateService_whenCommandIsValid() {
        Material material = MaterialMock.withSufficientStock();
        ServiceData savedService = ServiceDataMock.withDefaultValues();
        ServiceMaterialRequest materialRequest = new ServiceMaterialRequest(MATERIAL_ID, 2);
        CreateServiceCommand command = new CreateServiceCommand(
                "Troca de óleo",
                "Troca de óleo e filtro",
                BigDecimal.valueOf(50.00),
                BigDecimal.valueOf(80.00),
                List.of(materialRequest)
        );

        when(materialRepository.findById(MATERIAL_ID)).thenReturn(Optional.of(material));
        when(serviceRepository.save(any(ServiceData.class))).thenReturn(savedService);

        ServiceResponse result = useCase.execute(command);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(savedService.getName());
    }

    @Test
    void shouldThrowNotFoundException_whenMaterialDoesNotExist() {
        UUID unknownMaterialId = UUID.fromString("00000000-0000-0000-0000-000000000099");
        ServiceMaterialRequest materialRequest = new ServiceMaterialRequest(unknownMaterialId, 2);
        CreateServiceCommand command = new CreateServiceCommand(
                "Troca de óleo",
                "Descrição",
                BigDecimal.valueOf(50.00),
                BigDecimal.valueOf(80.00),
                List.of(materialRequest)
        );

        when(materialRepository.findById(unknownMaterialId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(unknownMaterialId.toString());
    }

    @Test
    void shouldPersistService_whenCommandIsValid() {
        Material material = MaterialMock.withSufficientStock();
        ServiceData savedService = ServiceDataMock.withDefaultValues();
        ServiceMaterialRequest materialRequest = new ServiceMaterialRequest(MATERIAL_ID, 2);
        CreateServiceCommand command = new CreateServiceCommand(
                "Troca de óleo",
                "Troca de óleo e filtro",
                BigDecimal.valueOf(50.00),
                BigDecimal.valueOf(80.00),
                List.of(materialRequest)
        );

        when(materialRepository.findById(MATERIAL_ID)).thenReturn(Optional.of(material));
        when(serviceRepository.save(any(ServiceData.class))).thenReturn(savedService);

        useCase.execute(command);

        verify(serviceRepository).save(any(ServiceData.class));
    }

    @Test
    void shouldResolveMaterialFromRepository_whenCreatingService() {
        Material material = MaterialMock.withSufficientStock();
        ServiceData savedService = ServiceDataMock.withDefaultValues();
        ServiceMaterialRequest materialRequest = new ServiceMaterialRequest(MATERIAL_ID, 2);
        CreateServiceCommand command = new CreateServiceCommand(
                "Troca de óleo",
                "Troca de óleo e filtro",
                BigDecimal.valueOf(50.00),
                BigDecimal.valueOf(80.00),
                List.of(materialRequest)
        );

        when(materialRepository.findById(MATERIAL_ID)).thenReturn(Optional.of(material));
        when(serviceRepository.save(any(ServiceData.class))).thenReturn(savedService);

        useCase.execute(command);

        verify(materialRepository).findById(MATERIAL_ID);
    }
}
