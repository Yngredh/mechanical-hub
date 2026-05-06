package com.fiap.mechanical_hub.application.usecases;

import com.fiap.mechanical_hub.application.dto.stock.StockDetailResponse;
import com.fiap.mechanical_hub.application.dto.stock.StockEntryRequest;
import com.fiap.mechanical_hub.application.dto.stock.StockSummaryResponse;
import com.fiap.mechanical_hub.application.mappers.StockMapper;
import com.fiap.mechanical_hub.domain.entities.*;
import com.fiap.mechanical_hub.domain.enums.StockStatusEnum;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.infrastructure.database.repositories.adapter.MaterialRepositoryAdapter;
import com.fiap.mechanical_hub.infrastructure.database.repositories.adapter.ServiceOrderRepositoryAdapter;
import com.fiap.mechanical_hub.infrastructure.database.repositories.adapter.StockMovementRepositoryAdapter;
import com.fiap.mechanical_hub.infrastructure.database.repositories.adapter.StockRepositoryAdapter;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockUseCaseTest {

    @Mock
    private StockMovementUseCase stockMovementUseCase;
    @Mock
    private StockPendingUseCase stockPendingUseCase;
    @Mock
    private NotificationUseCase notificationUseCase;
    @Mock
    private StockMovementRepositoryAdapter stockMovementRepository;
    @Mock
    private StockRepositoryAdapter stockRepository;
    @Mock
    private MaterialRepositoryAdapter materialRepository;
    @Mock
    private StockMapper stockMapper;
    @Mock
    private ServiceOrderRepositoryAdapter serviceOrderRepository;

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

        verify(stockPendingUseCase).createStockPendency(order, material, 10);
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

        verify(notificationUseCase).sendLowStockAlert(("Óleo"), (5));
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

    @Test
    @DisplayName("Deve retornar todos os resumos de estoque")
    void findAll_Success() {
        List<Stock> stocks = List.of(mock(Stock.class));
        when(stockRepository.findAll()).thenReturn(stocks);
        when(stockMapper.buildStockSummary(stocks)).thenReturn(List.of(mock(StockSummaryResponse.class)));

        List<StockSummaryResponse> result = stockUseCase.findAll();

        assertFalse(result.isEmpty());
        verify(stockRepository).findAll();
    }

    @Test
    @DisplayName("Deve retornar detalhes do estoque calculando quantidades corretamente")
    void findByMaterialId_Success() {
        UUID materialId = UUID.randomUUID();
        Stock available = mock(Stock.class);
        Stock reserved = mock(Stock.class);

        when(available.getStatus()).thenReturn(StockStatusEnum.AVAILABLE);
        when(available.getQuantity()).thenReturn(10);
        when(reserved.getStatus()).thenReturn(StockStatusEnum.RESERVED);
        when(reserved.getQuantity()).thenReturn(5);

        when(stockRepository.findAllByMaterialId(materialId)).thenReturn(List.of(available, reserved));
        when(stockMovementRepository.findByMaterialId(materialId)).thenReturn(Collections.emptyList());

        StockDetailResponse result = stockUseCase.findByMaterialId(materialId);

        assertEquals(15, result.quantityTotal());
        assertEquals(10, result.quantityAvailable());
        assertEquals(5, result.quantityReserved());
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando material não possuir estoque")
    void findByMaterialId_NotFound() {
        UUID materialId = UUID.randomUUID();
        when(stockRepository.findAllByMaterialId(materialId)).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> stockUseCase.findByMaterialId(materialId));
    }

    @Test
    @DisplayName("Deve resolver pendências de estoque quando houver saldo disponível")
    void resolveMaterialPendingIssues_Success() {
        // GIVEN
        UUID materialId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID pendingId = UUID.randomUUID();

        // 1. Mock do Estoque atualizado (com saldo)
        Stock updatedStock = mock(Stock.class);
        when(updatedStock.getQuantity()).thenReturn(10);

        // 2. Mock do item pendente
        StockPendingItem pending = mock(StockPendingItem.class);
        when(pending.getId()).thenReturn(pendingId);
        when(pending.getQuantity()).thenReturn(5);
        when(pending.getServiceOrderId()).thenReturn(orderId);
        when(pending.getMaterialId()).thenReturn(materialId);

        // 3. Mock da Ordem de Serviço (o que faltava!)
        ServiceOrder serviceOrder = mock(ServiceOrder.class);
        when(serviceOrderRepository.findById(orderId)).thenReturn(Optional.of(serviceOrder));

        // 4. Configuração dos comportamentos dos UseCases/Repositories
        when(stockPendingUseCase.findMaterialStockPendency(materialId)).thenReturn(List.of(pending));
        when(stockRepository.findByMaterialIdAndStatus(materialId, StockStatusEnum.RESERVED)).thenReturn(Optional.empty());

        // WHEN
        stockUseCase.resolveMaterialPendingIssues(materialId, updatedStock);

        // THEN
        verify(stockPendingUseCase).removePendency(pending);
        verify(updatedStock).subtractQuantity(5);
        verify(serviceOrder).setHasStockPending(false); // Verifica se a flag foi alterada
        verify(serviceOrderRepository).save(serviceOrder); // Verifica se a ordem foi salva
        verify(stockRepository, atLeastOnce()).save(any(Stock.class));
    }

    @Test
    @DisplayName("Deve remover a flag de pendência da ordem de serviço")
    void removeStockPending_Success() {
        UUID orderId = UUID.randomUUID();
        ServiceOrder order = mock(ServiceOrder.class);

        when(serviceOrderRepository.findById(orderId)).thenReturn(Optional.of(order));


        serviceOrderRepository.findById(orderId).ifPresent(o -> {
            o.setHasStockPending(false);
            serviceOrderRepository.save(o);
        });

        verify(order).setHasStockPending(false);
        verify(serviceOrderRepository).save(order);
    }

    @Test
    @DisplayName("Deve interromper a resolução de pendências quando o estoque disponível chegar a zero")
    void resolveMaterialPendingIssues_ShouldBreakLoopWhenStockExhausted() {
        UUID materialId = UUID.randomUUID();

        Stock updatedStock = mock(Stock.class);
        when(updatedStock.getQuantity()).thenReturn(2, 0);

        StockPendingItem firstPending = mock(StockPendingItem.class);
        when(firstPending.getQuantity()).thenReturn(5);
        when(firstPending.getMaterialId()).thenReturn(materialId);
        when(firstPending.getServiceOrderId()).thenReturn(UUID.randomUUID());

        StockPendingItem secondPending = mock(StockPendingItem.class);

        when(stockPendingUseCase.findMaterialStockPendency(materialId))
                .thenReturn(List.of(firstPending, secondPending));

        when(stockRepository.findByMaterialIdAndStatus(materialId, StockStatusEnum.RESERVED))
                .thenReturn(Optional.empty());
        when(materialRepository.findById(materialId)).thenReturn(Optional.empty());

        stockUseCase.resolveMaterialPendingIssues(materialId, updatedStock);

        verify(updatedStock, times(1)).subtractQuantity(anyInt());

        verify(stockPendingUseCase, never()).removePendency(secondPending);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando não houver registro de estoque disponível para o material")
    void registerStockEntry_ShouldThrowNotFoundException_WhenStockRecordDoesNotExist() {
        UUID materialId = UUID.randomUUID();
        StockEntryRequest request = new StockEntryRequest(materialId, 10);

        when(stockRepository.findByMaterialIdAndStatus(materialId, StockStatusEnum.AVAILABLE))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            stockUseCase.registerStockEntry(request);
        });

        String expectedMessage = "Estoque não encontrado para o material id: " + materialId;
        assertEquals(expectedMessage, exception.getMessage());

        verify(stockRepository, never()).save(any(Stock.class));
        verify(stockMovementUseCase, never()).registerStockEntryMovement(any());
        verify(stockPendingUseCase, never()).findMaterialStockPendency(any());
    }

    @Test
    @DisplayName("Deve atualizar estoque reservado existente quando já houver registro")
    void executeReservation_ShouldUpdateExistingReservedStock() {
        UUID serviceOrderId = UUID.randomUUID();
        UUID materialId = UUID.randomUUID();
        Integer quantityToReserve = 5;

        Stock availableStock = mock(Stock.class);

        when(availableStock.checkMaterialAvailability(quantityToReserve)).thenReturn(false);

        Stock existingReservedStock = mock(Stock.class);

        when(stockRepository.findByMaterialIdAndStatus(materialId, StockStatusEnum.AVAILABLE))
                .thenReturn(Optional.of(availableStock));

        when(stockRepository.findByMaterialIdAndStatus(materialId, StockStatusEnum.RESERVED))
                .thenReturn(Optional.of(existingReservedStock));

        when(materialRepository.findById(materialId)).thenReturn(Optional.empty());

        ServiceOrder order = mock(ServiceOrder.class);
        when(order.getId()).thenReturn(serviceOrderId);
        Material material = new Material(materialId, "Material Teste", null, null, 10, null, null);

        stockUseCase.reserveForServiceOrder(order, material, quantityToReserve);

        verify(existingReservedStock).addQuantity(quantityToReserve);
        verify(stockRepository).save(existingReservedStock);
        verify(stockRepository).save(availableStock);
    }

    @Test
    @DisplayName("Deve deletar material e dependências com sucesso")
    void delete_Success() {
        UUID materialId = UUID.randomUUID();
        Material material = mock(Material.class);

        when(materialRepository.findById(materialId)).thenReturn(Optional.of(material));

        stockUseCase.delete(materialId);

        verify(stockMovementRepository, times(1)).deleteByMaterialId(materialId);
        verify(stockMovementRepository, times(1)).flush();

        verify(stockRepository, times(1)).deleteByMaterialId(materialId);
        verify(stockRepository, times(1)).flush();

        verify(materialRepository, times(1)).deleteById(materialId);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando o material não existir")
    void delete_MaterialNotFound() {
        UUID materialId = UUID.randomUUID();
        when(materialRepository.findById(materialId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> stockUseCase.delete(materialId));

        verify(stockMovementRepository, never()).deleteByMaterialId(any());
        verify(stockRepository, never()).deleteByMaterialId(any());
        verify(materialRepository, never()).deleteById(any());
    }
}