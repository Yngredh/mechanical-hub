package com.fiap.mechanical_hub.application.usecases.material;

import com.fiap.mechanical_hub.application.command.material.UpdateMaterialCommand;
import com.fiap.mechanical_hub.application.dto.material.MaterialResponse;
import com.fiap.mechanical_hub.domain.entities.Material;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.MaterialRepository;
import com.fiap.mechanical_hub.mocks.domain.entities.MaterialMock;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpdateMaterialUseCaseTest {

    private final MaterialRepository materialRepository = mock(MaterialRepository.class);
    private final UpdateMaterialUseCase useCase = new UpdateMaterialUseCase(materialRepository);

    @Test
    void shouldUpdateMaterialWithAllProperties_whenMaterialExists() {
        UUID materialId = UUID.fromString("00000000-0000-0000-0000-000000000020");
        Material existingMaterial = MaterialMock.withSufficientStock();
        UpdateMaterialCommand command = new UpdateMaterialCommand(
                materialId,
                "Óleo de motor atualizado",
                "Óleo 10W40 sintético",
                BigDecimal.valueOf(55.00),
                15
        );
        Material updatedMaterial = MaterialMock.withSufficientStock();
        updatedMaterial.update(
                command.name(),
                command.description(),
                command.unitPrice(),
                command.minStockQuantity()
        );

        when(materialRepository.findById(materialId)).thenReturn(Optional.of(existingMaterial));
        when(materialRepository.save(any(Material.class))).thenReturn(updatedMaterial);

        MaterialResponse result = useCase.execute(command);

        assertThat(result)
                .isNotNull()
                .satisfies(response -> {
                    assertThat(response.name()).isEqualTo("Óleo de motor atualizado");
                    assertThat(response.unitPrice()).isEqualTo(BigDecimal.valueOf(55.00));
                    assertThat(response.minStockQuantity()).isEqualTo(15);
                });
        verify(materialRepository).findById(materialId);
        verify(materialRepository).save(any(Material.class));
    }

    @Test
    void shouldThrowException_whenMaterialNotFound() {
        UUID materialId = UUID.randomUUID();
        UpdateMaterialCommand command = new UpdateMaterialCommand(
                materialId,
                "Filtro de ar",
                "Filtro de ar original",
                BigDecimal.valueOf(25.00),
                20
        );

        when(materialRepository.findById(materialId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("não encontrado");
    }

    @Test
    void shouldCallFindByIdAndSaveOnRepository_duringUpdate() {
        UUID materialId = UUID.fromString("00000000-0000-0000-0000-000000000020");
        Material existingMaterial = MaterialMock.withSufficientStock();
        UpdateMaterialCommand command = new UpdateMaterialCommand(
                materialId,
                "Novo nome",
                "Nova descrição",
                BigDecimal.valueOf(60.00),
                25
        );

        when(materialRepository.findById(materialId)).thenReturn(Optional.of(existingMaterial));
        when(materialRepository.save(any(Material.class))).thenReturn(existingMaterial);

        useCase.execute(command);

        verify(materialRepository).findById(materialId);
        verify(materialRepository).save(any(Material.class));
    }
}


