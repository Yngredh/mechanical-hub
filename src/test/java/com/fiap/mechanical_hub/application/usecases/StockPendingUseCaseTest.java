package com.fiap.mechanical_hub.application.usecases;

import com.fiap.mechanical_hub.application.repositories.StockPendingItemRepository;
import com.fiap.mechanical_hub.domain.entities.Material;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.entities.StockPendingItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockPendingUseCaseTest {

    @Mock
    private StockPendingItemRepository repository;

    @Mock
    private NotificationUseCase notificationUseCase;

    @InjectMocks
    private StockPendingUseCase stockPendingUseCase;

    @Test
    @DisplayName("Deve registrar uma nova pendência de estoque passando os objetos ServiceOrder e Material")
    void shouldRegisterPendingItem() {
        UUID serviceOrderId = UUID.randomUUID();
        UUID materialId = UUID.randomUUID();
        Integer quantity = 10;

        ServiceOrder order = new ServiceOrder();
        order.setId(serviceOrderId);
        order.setOrderNumber("OS-2024-001");

        Material material = new Material(
                materialId,
                "Pastilha de Freio",
                "Peça de reposição",
                new BigDecimal("150.00"),
                5,
                null,
                null
        );

        when(repository.save(any(StockPendingItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        stockPendingUseCase.createStockPendency(order, material, quantity);

        ArgumentCaptor<StockPendingItem> captor = ArgumentCaptor.forClass(StockPendingItem.class);
        verify(repository).save(captor.capture());

        StockPendingItem saved = captor.getValue();

        assertThat(saved.getServiceOrderId()).isEqualTo(serviceOrderId);
        assertThat(saved.getMaterialId()).isEqualTo(materialId);
        assertThat(saved.getQuantity()).isEqualTo(quantity);

    }


    @Test
    @DisplayName("Deve buscar pendências por ID de material ordenadas por data")
    void shouldFindByMaterialId() {
        UUID materialId = UUID.randomUUID();
        List<StockPendingItem> mockItems = List.of(
                StockPendingItem.create(UUID.randomUUID(), 5, materialId),
                StockPendingItem.create(UUID.randomUUID(), 3, materialId)
        );

        when(repository.findByMaterialIdOrderByCreatedAtAsc(materialId)).thenReturn(mockItems);

        List<StockPendingItem> result = stockPendingUseCase.findMaterialStockPendency(materialId);

        assertThat(result).hasSize(2);
        verify(repository).findByMaterialIdOrderByCreatedAtAsc(materialId);
    }

    @Test
    @DisplayName("Deve remover uma pendência de estoque")
    void shouldDeletePendingItem() {
        StockPendingItem item = StockPendingItem.create(UUID.randomUUID(), 1, UUID.randomUUID());

        stockPendingUseCase.removePendency(item);

        verify(repository, times(1)).delete(item);
    }

}