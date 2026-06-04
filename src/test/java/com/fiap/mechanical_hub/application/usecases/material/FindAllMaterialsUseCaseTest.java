package com.fiap.mechanical_hub.application.usecases.material;

import com.fiap.mechanical_hub.application.dto.material.MaterialResponse;
import com.fiap.mechanical_hub.domain.entities.Material;
import com.fiap.mechanical_hub.domain.repositories.MaterialRepository;
import com.fiap.mechanical_hub.mocks.domain.entities.MaterialMock;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FindAllMaterialsUseCaseTest {

    private final MaterialRepository materialRepository = mock(MaterialRepository.class);
    private final FindAllMaterialsUseCase useCase = new FindAllMaterialsUseCase(materialRepository);

    @Test
    void shouldReturnAllMaterials_whenMaterialsExist() {
        List<Material> materials = new ArrayList<>();
        materials.add(MaterialMock.withSufficientStock());
        materials.add(MaterialMock.withInsufficientStock());

        when(materialRepository.findAll()).thenReturn(materials);

        List<MaterialResponse> result = useCase.execute();

        assertThat(result)
                .isNotNull()
                .hasSize(2);
    }

    @Test
    void shouldReturnEmptyList_whenNoMaterialsExist() {
        List<Material> materials = new ArrayList<>();

        when(materialRepository.findAll()).thenReturn(materials);

        List<MaterialResponse> result = useCase.execute();

        assertThat(result)
                .isNotNull()
                .isEmpty();
    }

    @Test
    void shouldCallFindAllOnRepository() {
        List<Material> materials = new ArrayList<>();
        materials.add(MaterialMock.withSufficientStock());

        when(materialRepository.findAll()).thenReturn(materials);

        useCase.execute();

        verify(materialRepository).findAll();
    }

    @Test
    void shouldMapMaterialsToResponse_correctly() {
        Material material1 = MaterialMock.withSufficientStock();
        Material material2 = MaterialMock.withInsufficientStock();
        List<Material> materials = new ArrayList<>();
        materials.add(material1);
        materials.add(material2);

        when(materialRepository.findAll()).thenReturn(materials);

        List<MaterialResponse> result = useCase.execute();

        assertThat(result)
                .isNotNull()
                .hasSize(2)
                .satisfies(list -> {
                    assertThat(list.get(0).name()).isEqualTo(material1.getName());
                    assertThat(list.get(1).name()).isEqualTo(material2.getName());
                });
    }

    @Test
    void shouldReturnResponseListWithMaterialData() {
        Material material = MaterialMock.withSufficientStock();
        List<Material> materials = new ArrayList<>();
        materials.add(material);

        when(materialRepository.findAll()).thenReturn(materials);

        List<MaterialResponse> result = useCase.execute();

        assertThat(result)
                .isNotNull()
                .hasSize(1)
                .satisfies(list -> {
                    assertThat(list.get(0).id()).isNotNull();
                    assertThat(list.get(0).createdAt()).isNotNull();
                    assertThat(list.get(0).updatedAt()).isNotNull();
                });
    }

    @Test
    void shouldReturnMultipleMaterials_withCorrectProperties() {
        Material material1 = MaterialMock.withPrice(java.math.BigDecimal.valueOf(45.00));
        Material material2 = MaterialMock.withMinStockQuantity(15);
        List<Material> materials = new ArrayList<>();
        materials.add(material1);
        materials.add(material2);

        when(materialRepository.findAll()).thenReturn(materials);

        List<MaterialResponse> result = useCase.execute();

        assertThat(result)
                .isNotNull()
                .hasSize(2)
                .satisfies(list -> {
                    assertThat(list.get(0).unitPrice()).isEqualTo(java.math.BigDecimal.valueOf(45.00));
                    assertThat(list.get(1).minStockQuantity()).isEqualTo(15);
                });
    }
}






