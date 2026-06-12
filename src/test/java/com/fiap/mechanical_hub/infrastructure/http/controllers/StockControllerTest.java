package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fiap.mechanical_hub.application.command.stock.DeleteStockCommand;
import com.fiap.mechanical_hub.application.command.stock.RegisterStockEntryCommand;
import com.fiap.mechanical_hub.application.dto.stock.StockDetailResponse;
import com.fiap.mechanical_hub.application.dto.stock.StockEntryRequest;
import com.fiap.mechanical_hub.application.dto.stock.StockSummaryResponse;
import com.fiap.mechanical_hub.application.mappers.StockMapper;
import com.fiap.mechanical_hub.application.usecases.stock.DeleteStockUseCase;
import com.fiap.mechanical_hub.application.usecases.stock.FindAllStocksUseCase;
import com.fiap.mechanical_hub.application.usecases.stock.FindStockByMaterialIdUseCase;
import com.fiap.mechanical_hub.application.usecases.stock.FindStockMovementsByMaterialId;
import com.fiap.mechanical_hub.application.usecases.stock.RegisterStockEntryUseCase;
import com.fiap.mechanical_hub.domain.entities.Stock;
import com.fiap.mechanical_hub.domain.entities.StockMovement;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StockControllerTest {

    private static final UUID MATERIAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private final FindStockMovementsByMaterialId findStockMovementsByMaterialId = mock(FindStockMovementsByMaterialId.class);
    private final RegisterStockEntryUseCase registerStockEntryUseCase = mock(RegisterStockEntryUseCase.class);
    private final FindAllStocksUseCase findAllStocksUseCase = mock(FindAllStocksUseCase.class);
    private final FindStockByMaterialIdUseCase findStockByMaterialIdUseCase = mock(FindStockByMaterialIdUseCase.class);
    private final DeleteStockUseCase deleteStockUseCase = mock(DeleteStockUseCase.class);
    private final StockMapper stockHttpMapper = mock(StockMapper.class);

    private final StockController controller = new StockController(
            findStockMovementsByMaterialId, registerStockEntryUseCase, findAllStocksUseCase,
            findStockByMaterialIdUseCase, deleteStockUseCase, stockHttpMapper
    );

    @Test
    void shouldReturnNoContent_whenRegisteringStockEntry() {
        StockEntryRequest request = new StockEntryRequest(MATERIAL_ID, 10);
        when(stockHttpMapper.toRegisterStockEntryCommand(request)).thenReturn(mock(RegisterStockEntryCommand.class));

        ResponseEntity<Void> response = controller.registerEntry(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(registerStockEntryUseCase).execute(any());
    }

    @Test
    void shouldReturnOk_whenFindingAllStocks() {
        when(findAllStocksUseCase.execute()).thenReturn(List.of());
        when(stockHttpMapper.buildStockSummary(any())).thenReturn(List.of(mock(StockSummaryResponse.class)));

        ResponseEntity<List<StockSummaryResponse>> response = controller.findAll();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void shouldReturnOk_whenFindingStockByMaterialId() {
        when(findStockByMaterialIdUseCase.execute(MATERIAL_ID)).thenReturn(List.of());
        when(findStockMovementsByMaterialId.execute(MATERIAL_ID)).thenReturn(List.of());
        when(stockHttpMapper.toDetailResponse(any(), any(), any())).thenReturn(mock(StockDetailResponse.class));

        ResponseEntity<StockDetailResponse> response = controller.findByMaterialId(MATERIAL_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldReturnNoContent_whenDeletingStock() {
        when(stockHttpMapper.toDeleteCommand(MATERIAL_ID)).thenReturn(mock(DeleteStockCommand.class));

        ResponseEntity<Void> response = controller.delete(MATERIAL_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(deleteStockUseCase).execute(any());
    }
}
