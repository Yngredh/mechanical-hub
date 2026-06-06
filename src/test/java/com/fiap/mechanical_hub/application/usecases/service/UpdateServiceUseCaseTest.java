package com.fiap.mechanical_hub.application.usecases.service;

import com.fiap.mechanical_hub.application.command.ordertask.UpdateServiceCommand;
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

class UpdateServiceUseCaseTest {

    private static final UUID SERVICE_ID = UUID.fromString("00000000-0000-0000-0000-000000000040");
    private static final UUID MATERIAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000020");

    private final MaterialRepository materialRepository = mock(MaterialRepository.class);
    private final ServiceRepository serviceRepository = mock(ServiceRepository.class);
    private final UpdateServiceUseCase useCase = new UpdateServiceUseCase(materialRepository, serviceRepository);

    @Test
    void shouldUpdateService_whenServiceExistsAndMaterialsAreValid() {
        ServiceData existingService = ServiceDataMock.withDefaultValues();
        Material material = MaterialMock.withSufficientStock();
        ServiceMaterialRequest materialRequest = new ServiceMaterialRequest(MATERIAL_ID, 3);
        UpdateServiceCommand command = new UpdateServiceCommand(
                SERVICE_ID,
                "Troca de filtro",
                "Troca de filtro de ar",
                BigDecimal.valueOf(30.00),
                BigDecimal.valueOf(60.00),
                List.of(materialRequest)
        );

        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(existingService));
        when(materialRepository.findById(MATERIAL_ID)).thenReturn(Optional.of(material));
        when(serviceRepository.save(any(ServiceData.class))).thenReturn(existingService);

        ServiceResponse result = useCase.execute(command);

        assertThat(result).isNotNull();
    }

    @Test
    void shouldThrowNotFoundException_whenServiceDoesNotExist() {
        UUID unknownServiceId = UUID.fromString("00000000-0000-0000-0000-000000000099");
        UpdateServiceCommand command = new UpdateServiceCommand(
                unknownServiceId,
                "Nome",
                "Descrição",
                BigDecimal.valueOf(30.00),
                BigDecimal.valueOf(60.00),
                List.of()
        );

        when(serviceRepository.findById(unknownServiceId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Serviço não encontrado");
    }

    @Test
    void shouldThrowNotFoundException_whenMaterialDoesNotExist() {
        UUID unknownMaterialId = UUID.fromString("00000000-0000-0000-0000-000000000099");
        ServiceData existingService = ServiceDataMock.withDefaultValues();
        ServiceMaterialRequest materialRequest = new ServiceMaterialRequest(unknownMaterialId, 1);
        UpdateServiceCommand command = new UpdateServiceCommand(
                SERVICE_ID,
                "Nome",
                "Descrição",
                BigDecimal.valueOf(30.00),
                BigDecimal.valueOf(60.00),
                List.of(materialRequest)
        );

        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(existingService));
        when(materialRepository.findById(unknownMaterialId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(unknownMaterialId.toString());
    }

    @Test
    void shouldPersistUpdatedService_whenUpdateSucceeds() {
        ServiceData existingService = ServiceDataMock.withDefaultValues();
        Material material = MaterialMock.withSufficientStock();
        ServiceMaterialRequest materialRequest = new ServiceMaterialRequest(MATERIAL_ID, 2);
        UpdateServiceCommand command = new UpdateServiceCommand(
                SERVICE_ID,
                "Novo nome",
                "Nova descrição",
                BigDecimal.valueOf(40.00),
                BigDecimal.valueOf(70.00),
                List.of(materialRequest)
        );

        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(existingService));
        when(materialRepository.findById(MATERIAL_ID)).thenReturn(Optional.of(material));
        when(serviceRepository.save(any(ServiceData.class))).thenReturn(existingService);

        useCase.execute(command);

        verify(serviceRepository).save(any(ServiceData.class));
    }
}
