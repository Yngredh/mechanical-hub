package com.fiap.mechanical_hub.application.usecases;

import com.fiap.mechanical_hub.application.dto.stock.StockEntryRequest;
import com.fiap.mechanical_hub.application.repositories.StockMovementRepository;
import com.fiap.mechanical_hub.domain.entities.StockMovement;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockMovementUseCaseTest {

    @Mock
    private StockMovementRepository stockMovementRepository;

    @InjectMocks
    private StockMovementUseCase stockMovementUseCase;

    @Test
    @DisplayName("Deve registrar um movimento de entrada de estoque com sucesso")
    void shouldRegisterStockEntryMovement() {
        UUID materialId = UUID.randomUUID();
        Integer quantity = 50;
        StockEntryRequest request = new StockEntryRequest(materialId, quantity);

        when(stockMovementRepository.save(any(StockMovement.class))).thenAnswer(i -> i.getArgument(0));

        stockMovementUseCase.registerStockEntryMovement(request);

        ArgumentCaptor<StockMovement> movementCaptor = ArgumentCaptor.forClass(StockMovement.class);
        verify(stockMovementRepository, times(1)).save(movementCaptor.capture());

        StockMovement capturedMovement = movementCaptor.getValue();
        assertThat(capturedMovement.getMaterialId()).isEqualTo(materialId);
        assertThat(capturedMovement.getQuantity()).isEqualTo(quantity);
        assertThat(capturedMovement.getMovementType()).isEqualTo("ENTRADA"); // Descrição do ENTRY
        assertThat(capturedMovement.getServiceOrderId()).isNull();
    }

    @Test
    @DisplayName("Deve registrar um movimento de retorno de estoque com sucesso")
    void shouldRegisterStockReturnMovement() {
        UUID materialId = UUID.randomUUID();
        UUID serviceOrderId = UUID.randomUUID();
        Integer quantity = 5;

        stockMovementUseCase.registerStockReturnMovement(materialId, serviceOrderId, quantity);

        ArgumentCaptor<StockMovement> movementCaptor = ArgumentCaptor.forClass(StockMovement.class);
        verify(stockMovementRepository, times(1)).save(movementCaptor.capture());

        StockMovement capturedMovement = movementCaptor.getValue();
        assertThat(capturedMovement.getMaterialId()).isEqualTo(materialId);
        assertThat(capturedMovement.getServiceOrderId()).isEqualTo(serviceOrderId);
        assertThat(capturedMovement.getQuantity()).isEqualTo(quantity);
        assertThat(capturedMovement.getMovementType()).isEqualTo("RETORNO"); // Descrição do RETURN
    }
}