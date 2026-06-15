package com.fiap.mechanical_hub.application.usecases.material;

import com.fiap.mechanical_hub.application.dto.material.MaterialResponse;
import com.fiap.mechanical_hub.domain.entities.Material;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.MaterialRepository;
import com.fiap.mechanical_hub.mocks.domain.entities.MaterialMock;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FindMaterialByIdUseCaseTest {

    private final MaterialRepository materialRepository = mock(MaterialRepository.class);
    private final FindMaterialByIdUseCase useCase = new FindMaterialByIdUseCase(materialRepository);

    @Test
    void shouldReturnMaterial_whenMaterialExists() {
        UUID materialId = UUID.fromString("00000000-0000-0000-0000-000000000020");
        Material material = MaterialMock.withSufficientStock();

        when(materialRepository.findById(materialId)).thenReturn(Optional.of(material));

        MaterialResponse result = useCase.execute(materialId);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Óleo de motor");
    }

    @Test
    void shouldThrowException_whenMaterialNotFound() {
        UUID materialId = UUID.randomUUID();

        when(materialRepository.findById(materialId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(materialId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("não encontrado");
    }

    @Test
    void shouldCallFindByIdOnRepository() {
        UUID materialId = UUID.fromString("00000000-0000-0000-0000-000000000020");
        Material material = MaterialMock.withSufficientStock();

        when(materialRepository.findById(materialId)).thenReturn(Optional.of(material));

        useCase.execute(materialId);

        verify(materialRepository).findById(materialId);
    }

    @Test
    void shouldMapMaterialToResponse_correctly() {
        UUID materialId = UUID.fromString("00000000-0000-0000-0000-000000000020");
        Material material = MaterialMock.withSufficientStock();

        when(materialRepository.findById(materialId)).thenReturn(Optional.of(material));

        MaterialResponse result = useCase.execute(materialId);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo(material.getName());
        assertThat(result.description()).isEqualTo(material.getDescription());
        assertThat(result.unitPrice()).isEqualTo(material.getUnitPrice());
        assertThat(result.minStockQuantity()).isEqualTo(material.getMinStockQuantity());
    }

    @Test
    void shouldThrowNotFoundException_withCorrectMessage() {
        UUID materialId = UUID.randomUUID();

        when(materialRepository.findById(materialId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(materialId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldReturnResponseWithMaterialData() {
        UUID materialId = UUID.fromString("00000000-0000-0000-0000-000000000020");
        Material material = MaterialMock.withSufficientStock();

        when(materialRepository.findById(materialId)).thenReturn(Optional.of(material));

        MaterialResponse result = useCase.execute(materialId);

        assertThat(result).isNotNull();
        assertThat(result.id()).isNotNull();
        assertThat(result.createdAt()).isNotNull();
        assertThat(result.updatedAt()).isNotNull();
        verify(materialRepository).findById(materialId);
    }
}

