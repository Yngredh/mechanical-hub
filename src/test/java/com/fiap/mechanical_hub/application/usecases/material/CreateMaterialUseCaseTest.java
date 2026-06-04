package com.fiap.mechanical_hub.application.usecases.material;

import com.fiap.mechanical_hub.application.command.material.CreateMaterialCommand;
import com.fiap.mechanical_hub.application.dto.material.MaterialResponse;
import com.fiap.mechanical_hub.domain.entities.Material;
import com.fiap.mechanical_hub.domain.repositories.MaterialRepository;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateMaterialUseCaseTest {

    private final MaterialRepository materialRepository = mock(MaterialRepository.class);
    private final CreateMaterialUseCase useCase = new CreateMaterialUseCase(materialRepository);

    @Test
    void shouldCreateMaterialWithAllProperties_whenCommandIsValid() {
        CreateMaterialCommand command = new CreateMaterialCommand(
                "Óleo de motor",
                "Óleo 5W30 sintético",
                BigDecimal.valueOf(45.00),
                10
        );
        Material savedMaterial = Material.create(
                command.name(),
                command.description(),
                command.unitPrice(),
                command.minStockQuantity()
        );
        when(materialRepository.save(any(Material.class))).thenReturn(savedMaterial);

        MaterialResponse result = useCase.execute(command);

        assertThat(result)
                .isNotNull()
                .satisfies(response -> {
                    assertThat(response.id()).isNotNull();
                    assertThat(response.name()).isEqualTo("Óleo de motor");
                    assertThat(response.description()).isEqualTo("Óleo 5W30 sintético");
                    assertThat(response.unitPrice()).isEqualTo(BigDecimal.valueOf(45.00));
                    assertThat(response.minStockQuantity()).isEqualTo(10);
                    assertThat(response.createdAt()).isNotNull();
                    assertThat(response.updatedAt()).isNotNull();
                });
        verify(materialRepository).save(any(Material.class));
    }

    @Test
    void shouldThrowException_whenCommandHasInvalidData() {
        CreateMaterialCommand command = new CreateMaterialCommand(
                "",
                "Descrição",
                BigDecimal.valueOf(-10.00),
                5
        );

        when(materialRepository.save(any(Material.class))).thenThrow(new IllegalArgumentException("Invalid material data"));

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldCallRepositorySave_afterMaterialCreation() {
        CreateMaterialCommand command = new CreateMaterialCommand(
                "Bateria",
                "Bateria 60Ah",
                BigDecimal.valueOf(300.00),
                5
        );
        Material savedMaterial = Material.create(
                command.name(),
                command.description(),
                command.unitPrice(),
                command.minStockQuantity()
        );
        when(materialRepository.save(any(Material.class))).thenReturn(savedMaterial);

        useCase.execute(command);

        verify(materialRepository).save(any(Material.class));
    }
}



