package com.fiap.mechanical_hub.application.mappers;

import com.fiap.mechanical_hub.application.command.stock.CreateStockForNewMaterialCommand;
import com.fiap.mechanical_hub.application.command.stock.RegisterStockEntryCommand;
import com.fiap.mechanical_hub.application.command.stock.ReserveStockForServiceOrderCommand;
import com.fiap.mechanical_hub.application.dto.stock.StockDetailResponse;
import com.fiap.mechanical_hub.application.dto.stock.StockEntryRequest;
import com.fiap.mechanical_hub.application.dto.stock.StockSummaryResponse;
import com.fiap.mechanical_hub.domain.entities.Material;
import com.fiap.mechanical_hub.domain.entities.Stock;
import com.fiap.mechanical_hub.domain.entities.StockMovement;
import com.fiap.mechanical_hub.domain.repositories.MaterialRepository;
import com.fiap.mechanical_hub.mocks.domain.entities.MaterialMock;
import com.fiap.mechanical_hub.mocks.domain.entities.StockMock;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StockMapperTest {

    private static final UUID MATERIAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000020");
    private static final UUID SERVICE_ORDER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID MOVEMENT_ID = UUID.fromString("00000000-0000-0000-0000-000000000080");

    private final MaterialRepository materialRepository = mock(MaterialRepository.class);
    private final StockMapper mapper = new StockMapper(materialRepository);

    @Test
    void shouldCreateCreateStockCommand_whenMappingMaterialId() {
        CreateStockForNewMaterialCommand command = mapper.toCreateStockCommand(MATERIAL_ID);

        assertThat(command.materialId()).isEqualTo(MATERIAL_ID);
    }

    @Test
    void shouldCreateRegisterStockEntryCommand_whenMappingStockEntryRequest() {
        StockEntryRequest request = new StockEntryRequest(MATERIAL_ID, 10);

        RegisterStockEntryCommand command = mapper.toRegisterStockEntryCommand(request);

        assertThat(command.materialId()).isEqualTo(request.materialId());
        assertThat(command.quantity()).isEqualTo(request.quantity());
    }

    @Test
    void shouldCreateReserveCommand_whenMappingReserveParameters() {
        ReserveStockForServiceOrderCommand command = mapper.toReserveCommand(SERVICE_ORDER_ID, MATERIAL_ID, 5);

        assertThat(command.serviceOrderId()).isEqualTo(SERVICE_ORDER_ID);
        assertThat(command.materialId()).isEqualTo(MATERIAL_ID);
        assertThat(command.quantity()).isEqualTo(5);
    }

    @Test
    void shouldSumAvailableAndReservedQuantities_whenBuildingSummaryResponse() {
        Material material = MaterialMock.withSufficientStock();
        when(materialRepository.findById(MATERIAL_ID)).thenReturn(Optional.of(material));
        List<Stock> stocks = List.of(StockMock.available(8), StockMock.reserved(3));

        StockSummaryResponse summary = mapper.toSummaryResponse(MATERIAL_ID, stocks);

        assertThat(summary.quantityAvailable()).isEqualTo(8);
        assertThat(summary.quantityReserved()).isEqualTo(3);
        assertThat(summary.quantityTotal()).isEqualTo(11);
    }

    @Test
    void shouldUseMaterialName_whenBuildingSummaryResponse() {
        Material material = MaterialMock.withSufficientStock();
        when(materialRepository.findById(MATERIAL_ID)).thenReturn(Optional.of(material));

        StockSummaryResponse summary = mapper.toSummaryResponse(MATERIAL_ID, List.of(StockMock.available(5)));

        assertThat(summary.materialName()).isEqualTo(material.getName());
        assertThat(summary.materialId()).isEqualTo(MATERIAL_ID);
    }

    @Test
    void shouldThrowException_whenMaterialNotFoundInRepository() {
        when(materialRepository.findById(MATERIAL_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mapper.toSummaryResponse(MATERIAL_ID, List.of(StockMock.available(5))))
                .isInstanceOf(java.util.NoSuchElementException.class)
                .hasMessageContaining("Material não encontrado");
    }

    @Test
    void shouldGroupStocksByMaterial_whenBuildingStockSummaryForMultipleMaterials() {
        UUID materialId2 = UUID.fromString("00000000-0000-0000-0000-000000000021");
        Material material1 = MaterialMock.withSufficientStock();
        Material material2 = MaterialMock.withPrice(java.math.BigDecimal.valueOf(30.00));
        when(materialRepository.findById(MATERIAL_ID)).thenReturn(Optional.of(material1));
        when(materialRepository.findById(materialId2)).thenReturn(Optional.of(material2));

        Stock stock1 = new Stock(UUID.randomUUID(), MATERIAL_ID, 5, com.fiap.mechanical_hub.domain.enums.StockStatusEnum.AVAILABLE, LocalDateTime.now());
        Stock stock2 = new Stock(UUID.randomUUID(), materialId2, 3, com.fiap.mechanical_hub.domain.enums.StockStatusEnum.AVAILABLE, LocalDateTime.now());

        List<StockSummaryResponse> result = mapper.buildStockSummary(List.of(stock1, stock2));

        assertThat(result).hasSize(2);
    }

    @Test
    void shouldMapAllStockAndMovementFields_whenBuildingDetailResponse() {
        StockMovement movement = new StockMovement(
                MOVEMENT_ID, MATERIAL_ID, SERVICE_ORDER_ID, "reserva", 5, LocalDateTime.now()
        );
        List<Stock> stocks = List.of(StockMock.available(10), StockMock.reserved(2));

        StockDetailResponse detail = mapper.toDetailResponse(MATERIAL_ID, stocks, List.of(movement));

        assertThat(detail.materialId()).isEqualTo(MATERIAL_ID);
        assertThat(detail.quantityAvailable()).isEqualTo(10);
        assertThat(detail.quantityReserved()).isEqualTo(2);
        assertThat(detail.quantityTotal()).isEqualTo(12);
        assertThat(detail.movements()).hasSize(1);
        assertThat(detail.movements().getFirst().movementType()).isEqualTo(movement.getMovementType());
    }
}
