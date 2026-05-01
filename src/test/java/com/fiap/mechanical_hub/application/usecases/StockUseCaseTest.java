package com.fiap.mechanical_hub.application.usecases;

import com.fiap.mechanical_hub.application.dto.stock.StockEntryRequest;
import com.fiap.mechanical_hub.application.repositories.MaterialRepository;
import com.fiap.mechanical_hub.application.repositories.StockMovementRepository;
import com.fiap.mechanical_hub.application.repositories.StockRepository;
import com.fiap.mechanical_hub.domain.entities.*;
import com.fiap.mechanical_hub.domain.enums.StockStatusEnum;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockUseCaseTest {

    @Mock private StockMovementUseCase stockMovementUseCase;
    @Mock private StockPendingUseCase stockPendingUseCase;
    @Mock private NotificationUseCase notificationUseCase;
    @Mock private StockMovementRepository stockMovementRepository;
    @Mock private StockRepository stockRepository;
    @Mock private MaterialRepository materialRepository;

    @InjectMocks
    private StockUseCase stockUseCase;

    @Test
    @DisplayName("Deve inicializar estoque para novo material")
    void shouldSetStockForNewMaterial() {
        UUID materialId = UUID.randomUUID();
        stockUseCase.setStockForNewMaterial(materialId);
        verify(stockRepository, times(1)).save(any(Stock.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar detalhe de material inexistente")
    void shouldThrowExceptionWhenMaterialNotFoundInStock() {
        UUID materialId = UUID.randomUUID();
        when(stockRepository.findAllByMaterialId(materialId)).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> stockUseCase.findByMaterialId(materialId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Deve registrar entrada de estoque e resolver pendências")
    void shouldRegisterStockEntryAndResolveIssues() {
        UUID materialId = UUID.randomUUID();
        StockEntryRequest request = new StockEntryRequest(materialId, 50);
        Stock existingStock = new Stock(UUID.randomUUID(), materialId, 10, StockStatusEnum.AVAILABLE, null);

        when(stockRepository.findByMaterialIdAndStatus(materialId, StockStatusEnum.AVAILABLE))
                .thenReturn(Optional.of(existingStock));
        when(stockRepository.save(any(Stock.class))).thenAnswer(i -> i.getArgument(0));
        when(stockPendingUseCase.findMaterialStockPendency(materialId)).thenReturn(Collections.emptyList());

        stockUseCase.registerStockEntry(request);

        assertThat(existingStock.getQuantity()).isEqualTo(60);
        verify(stockMovementUseCase).registerStockEntryMovement(request);
        verify(stockPendingUseCase).findMaterialStockPendency(materialId);
    }

    @Test
    @DisplayName("Deve criar pendência quando não houver estoque disponível para reserva")
    void shouldCreatePendencyWhenStockIsInsufficient() {
        ServiceOrder order = new ServiceOrder();
        Material material = new Material(UUID.randomUUID(), "Parafuso", "", BigDecimal.ONE, 5, null, null);
        Stock lowStock = new Stock(UUID.randomUUID(), material.getId(), 2, StockStatusEnum.AVAILABLE, null);

        when(stockRepository.findByMaterialIdAndStatus(material.getId(), StockStatusEnum.AVAILABLE))
                .thenReturn(Optional.of(lowStock));

        boolean hasPendency = stockUseCase.reserveForServiceOrder(order, material, 10);

        assertThat(hasPendency).isTrue();
        verify(stockPendingUseCase).createStockPendency(eq(order), eq(material), eq(10));
    }

    @Test
    @DisplayName("Deve enviar alerta de estoque baixo após reserva")
    void shouldSendAlertWhenStockBelowMinimumAfterReservation() {
        UUID mId = UUID.randomUUID();
        ServiceOrder order = new ServiceOrder();
        order.setId(UUID.randomUUID());

        Material material = new Material(mId, "Óleo", "", BigDecimal.TEN, 5, null, null);
        Stock stock = new Stock(UUID.randomUUID(), mId, 10, StockStatusEnum.AVAILABLE, null);

        when(stockRepository.findByMaterialIdAndStatus(mId, StockStatusEnum.AVAILABLE)).thenReturn(Optional.of(stock));
        when(materialRepository.findById(mId)).thenReturn(Optional.of(material));

        stockUseCase.reserveForServiceOrder(order, material, 6);

        verify(notificationUseCase).sendLowStockAlert(eq("Óleo"), eq(5));
    }

    @Test
    @DisplayName("Deve restaurar itens reservados para a OS")
    void shouldRestoreReservedItems() {
        UUID materialId = UUID.randomUUID();
        Material material = new Material(materialId, "Filtro", "", BigDecimal.ZERO, 0, null, null);

        ServiceMaterial sm = ServiceMaterial.builder()
                .material(material)
                .quantity(5)
                        .build();

        ServiceData sd = ServiceData.builder()
                .materials(List.of(sm))
                .build();

         var task = OrderTask.builder()
                 .id(UUID.randomUUID())
                 .serviceData(sd)
                .build();

        ServiceOrder order = new ServiceOrder();
        order.setOrderTasks(List.of(task));

        Stock reservedStock = new Stock(UUID.randomUUID(), materialId, 5, StockStatusEnum.RESERVED, null);
        Stock availableStock = new Stock(UUID.randomUUID(), materialId, 0, StockStatusEnum.AVAILABLE, null);

        when(stockRepository.findByMaterialIdAndStatus(materialId, StockStatusEnum.RESERVED)).thenReturn(Optional.of(reservedStock));
        when(stockRepository.findByMaterialIdAndStatus(materialId, StockStatusEnum.AVAILABLE)).thenReturn(Optional.of(availableStock));

        stockUseCase.restoreReservedItems(order);

        assertThat(reservedStock.getQuantity()).isZero();
        assertThat(availableStock.getQuantity()).isEqualTo(5);
        verify(stockMovementUseCase).registerStockReturnMovement(any(), any(), any());
    }
}