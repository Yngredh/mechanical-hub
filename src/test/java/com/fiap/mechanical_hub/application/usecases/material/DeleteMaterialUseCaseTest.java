package com.fiap.mechanical_hub.application.usecases.material;

import com.fiap.mechanical_hub.domain.entities.Material;
import com.fiap.mechanical_hub.domain.entities.ServiceData;
import com.fiap.mechanical_hub.domain.entities.ServiceMaterial;
import com.fiap.mechanical_hub.domain.entities.StockPendingItem;
import com.fiap.mechanical_hub.domain.exceptions.BusinessRuleException;
import com.fiap.mechanical_hub.domain.exceptions.MaterialNotFoundException;
import com.fiap.mechanical_hub.domain.repositories.MaterialRepository;
import com.fiap.mechanical_hub.domain.repositories.ServiceMaterialRepository;
import com.fiap.mechanical_hub.domain.repositories.ServiceRepository;
import com.fiap.mechanical_hub.domain.repositories.StockPendingItemRepository;
import com.fiap.mechanical_hub.mocks.domain.entities.MaterialMock;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeleteMaterialUseCaseTest {

    private final MaterialRepository materialRepository = mock(MaterialRepository.class);
    private final ServiceMaterialRepository serviceMaterialRepository = mock(ServiceMaterialRepository.class);
    private final ServiceRepository serviceRepository = mock(ServiceRepository.class);
    private final StockPendingItemRepository stockPendingItemRepository = mock(StockPendingItemRepository.class);
    private final DeleteMaterialUseCase useCase = new DeleteMaterialUseCase(
            materialRepository,
            serviceMaterialRepository,
            serviceRepository,
            stockPendingItemRepository
    );

    @Test
    void shouldDeleteMaterial_whenMaterialExistsAndHasNoConstraints() {
        UUID materialId = UUID.fromString("00000000-0000-0000-0000-000000000020");
        Material material = MaterialMock.withSufficientStock();

        when(materialRepository.findById(materialId)).thenReturn(Optional.of(material));
        when(stockPendingItemRepository.findByMaterialIdOrderByCreatedAtAsc(materialId)).thenReturn(new ArrayList<>());
        when(serviceMaterialRepository.findByMaterialId(materialId)).thenReturn(new ArrayList<>());
        when(serviceRepository.findAllIn(new ArrayList<>())).thenReturn(new ArrayList<>());

        assertThatCode(() -> useCase.execute(materialId))
                .doesNotThrowAnyException();

        verify(materialRepository).findById(materialId);
        verify(stockPendingItemRepository).findByMaterialIdOrderByCreatedAtAsc(materialId);
        verify(materialRepository).save(material);
    }

    @Test
    void shouldThrowException_whenMaterialNotFound() {
        UUID materialId = UUID.randomUUID();

        when(materialRepository.findById(materialId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(materialId))
                .isInstanceOf(MaterialNotFoundException.class);
    }

    @Test
    void shouldThrowException_whenMaterialHasStockPendingItems() {
        UUID materialId = UUID.fromString("00000000-0000-0000-0000-000000000020");
        Material material = MaterialMock.withSufficientStock();
        StockPendingItem pendingItem = new StockPendingItem();

        List<StockPendingItem> pendingItems = new ArrayList<>();
        pendingItems.add(pendingItem);

        when(materialRepository.findById(materialId)).thenReturn(Optional.of(material));
        when(stockPendingItemRepository.findByMaterialIdOrderByCreatedAtAsc(materialId)).thenReturn(pendingItems);

        assertThatThrownBy(() -> useCase.execute(materialId))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("pendências de estoque");
    }

    @Test
    void shouldThrowException_whenMaterialHasActiveServices() {
        UUID materialId = UUID.fromString("00000000-0000-0000-0000-000000000020");
        Material material = MaterialMock.withSufficientStock();
        UUID serviceId = UUID.randomUUID();
        ServiceMaterial serviceMaterial = ServiceMaterial.builder()
                .serviceId(serviceId)
                .material(material)
                .quantity(1)
                .build();

        List<ServiceMaterial> serviceMaterials = new ArrayList<>();
        serviceMaterials.add(serviceMaterial);

        ServiceData activeService = ServiceData.builder()
                .id(serviceId)
                .name("Serviço ativo")
                .active(true)
                .build();

        List<ServiceData> services = new ArrayList<>();
        services.add(activeService);

        when(materialRepository.findById(materialId)).thenReturn(Optional.of(material));
        when(stockPendingItemRepository.findByMaterialIdOrderByCreatedAtAsc(materialId)).thenReturn(new ArrayList<>());
        when(serviceMaterialRepository.findByMaterialId(materialId)).thenReturn(serviceMaterials);
        when(serviceRepository.findAllIn(List.of(serviceId))).thenReturn(services);

        assertThatThrownBy(() -> useCase.execute(materialId))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("serviços ativos");
    }

    @Test
    void shouldDeactivateMaterial_beforeSaving() {
        UUID materialId = UUID.fromString("00000000-0000-0000-0000-000000000020");
        Material material = MaterialMock.withSufficientStock();

        when(materialRepository.findById(materialId)).thenReturn(Optional.of(material));
        when(stockPendingItemRepository.findByMaterialIdOrderByCreatedAtAsc(materialId)).thenReturn(new ArrayList<>());
        when(serviceMaterialRepository.findByMaterialId(materialId)).thenReturn(new ArrayList<>());
        when(serviceRepository.findAllIn(new ArrayList<>())).thenReturn(new ArrayList<>());

        useCase.execute(materialId);

        verify(materialRepository).save(material);
    }

    @Test
    void shouldCheckStockPendingItems_beforeProcessing() {
        UUID materialId = UUID.fromString("00000000-0000-0000-0000-000000000020");
        Material material = MaterialMock.withSufficientStock();

        when(materialRepository.findById(materialId)).thenReturn(Optional.of(material));
        when(stockPendingItemRepository.findByMaterialIdOrderByCreatedAtAsc(materialId)).thenReturn(new ArrayList<>());
        when(serviceMaterialRepository.findByMaterialId(materialId)).thenReturn(new ArrayList<>());
        when(serviceRepository.findAllIn(new ArrayList<>())).thenReturn(new ArrayList<>());

        useCase.execute(materialId);

        verify(stockPendingItemRepository).findByMaterialIdOrderByCreatedAtAsc(materialId);
    }

    @Test
    void shouldCheckServiceMaterials_beforeProcessing() {
        UUID materialId = UUID.fromString("00000000-0000-0000-0000-000000000020");
        Material material = MaterialMock.withSufficientStock();

        when(materialRepository.findById(materialId)).thenReturn(Optional.of(material));
        when(stockPendingItemRepository.findByMaterialIdOrderByCreatedAtAsc(materialId)).thenReturn(new ArrayList<>());
        when(serviceMaterialRepository.findByMaterialId(materialId)).thenReturn(new ArrayList<>());
        when(serviceRepository.findAllIn(new ArrayList<>())).thenReturn(new ArrayList<>());

        useCase.execute(materialId);

        verify(serviceMaterialRepository).findByMaterialId(materialId);
    }
}


