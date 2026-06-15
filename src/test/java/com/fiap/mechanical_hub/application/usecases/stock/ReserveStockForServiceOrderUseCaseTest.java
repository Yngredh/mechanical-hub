package com.fiap.mechanical_hub.application.usecases.stock;

import com.fiap.mechanical_hub.application.command.stock.ReserveStockForServiceOrderCommand;
import com.fiap.mechanical_hub.application.usecases.notifications.SendLowStockAlertUseCase;
import com.fiap.mechanical_hub.application.usecases.notifications.SendStockShortageAlertUseCase;
import com.fiap.mechanical_hub.domain.entities.Stock;
import com.fiap.mechanical_hub.domain.enums.StockStatusEnum;
import com.fiap.mechanical_hub.domain.repositories.MaterialRepository;
import com.fiap.mechanical_hub.domain.repositories.ServiceOrderRepository;
import com.fiap.mechanical_hub.domain.repositories.StockMovementRepository;
import com.fiap.mechanical_hub.domain.repositories.StockPendingItemRepository;
import com.fiap.mechanical_hub.domain.repositories.StockRepository;
import com.fiap.mechanical_hub.mocks.domain.entities.MaterialMock;
import com.fiap.mechanical_hub.mocks.domain.entities.ServiceOrderMock;
import com.fiap.mechanical_hub.mocks.domain.entities.StockMock;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReserveStockForServiceOrderUseCaseTest {

    private static final UUID MATERIAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000020");
    private static final UUID ORDER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private final StockRepository stockRepository = mock(StockRepository.class);
    private final StockMovementRepository stockMovementRepository = mock(StockMovementRepository.class);
    private final StockPendingItemRepository stockPendingItemRepository = mock(StockPendingItemRepository.class);
    private final MaterialRepository materialRepository = mock(MaterialRepository.class);
    private final ServiceOrderRepository serviceOrderRepository = mock(ServiceOrderRepository.class);
    private final SendLowStockAlertUseCase sendLowStockAlertUseCase = mock(SendLowStockAlertUseCase.class);
    private final SendStockShortageAlertUseCase sendStockShortageAlertUseCase = mock(SendStockShortageAlertUseCase.class);
    private final ReserveStockForServiceOrderUseCase useCase = new ReserveStockForServiceOrderUseCase(
            stockRepository, stockMovementRepository, stockPendingItemRepository,
            materialRepository, serviceOrderRepository, sendLowStockAlertUseCase, sendStockShortageAlertUseCase);

    @Test
    void shouldReturnAvailableStock_whenStockIsSufficient() {
        Stock available = StockMock.available(20);
        when(stockRepository.findByMaterialIdAndStatus(MATERIAL_ID, StockStatusEnum.AVAILABLE)).thenReturn(Optional.of(available));
        when(stockRepository.findByMaterialIdAndStatus(MATERIAL_ID, StockStatusEnum.RESERVED)).thenReturn(Optional.empty());
        when(stockRepository.save(any())).thenReturn(available);
        when(materialRepository.findById(MATERIAL_ID)).thenReturn(Optional.of(MaterialMock.withMinStockQuantity(5)));

        Stock result = useCase.execute(new ReserveStockForServiceOrderCommand(ORDER_ID, MATERIAL_ID, 5));

        assertThat(result).isNotNull();
        verify(stockMovementRepository).save(any());
    }

    @Test
    void shouldCreatePendencyAndReturnNull_whenStockIsInsufficient() {
        when(stockRepository.findByMaterialIdAndStatus(MATERIAL_ID, StockStatusEnum.AVAILABLE)).thenReturn(Optional.empty());
        when(materialRepository.findById(MATERIAL_ID)).thenReturn(Optional.of(MaterialMock.withSufficientStock()));
        when(serviceOrderRepository.findById(ORDER_ID)).thenReturn(Optional.of(ServiceOrderMock.inDiagnosis()));

        Stock result = useCase.execute(new ReserveStockForServiceOrderCommand(ORDER_ID, MATERIAL_ID, 5));

        assertThat(result).isNull();
        verify(stockPendingItemRepository).save(any());
    }

    @Test
    void shouldSendLowStockAlert_whenRemainingStockFallsBelowMinimum() {
        Stock available = StockMock.available(6);
        when(stockRepository.findByMaterialIdAndStatus(MATERIAL_ID, StockStatusEnum.AVAILABLE)).thenReturn(Optional.of(available));
        when(stockRepository.findByMaterialIdAndStatus(MATERIAL_ID, StockStatusEnum.RESERVED)).thenReturn(Optional.empty());
        when(stockRepository.save(any())).thenReturn(StockMock.available(1));
        when(materialRepository.findById(MATERIAL_ID)).thenReturn(Optional.of(MaterialMock.withMinStockQuantity(5)));

        useCase.execute(new ReserveStockForServiceOrderCommand(ORDER_ID, MATERIAL_ID, 5));

        verify(sendLowStockAlertUseCase).execute(any());
    }
}
