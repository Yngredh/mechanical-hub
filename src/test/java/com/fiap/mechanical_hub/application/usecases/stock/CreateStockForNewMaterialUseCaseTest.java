package com.fiap.mechanical_hub.application.usecases.stock;

import com.fiap.mechanical_hub.application.command.stock.CreateStockForNewMaterialCommand;
import com.fiap.mechanical_hub.domain.entities.Stock;
import com.fiap.mechanical_hub.domain.repositories.StockRepository;
import com.fiap.mechanical_hub.mocks.domain.entities.StockMock;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateStockForNewMaterialUseCaseTest {

    private static final UUID MATERIAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000020");

    private final StockRepository stockRepository = mock(StockRepository.class);
    private final CreateStockForNewMaterialUseCase useCase = new CreateStockForNewMaterialUseCase(stockRepository);

    @Test
    void shouldSaveAndReturnStock_whenCommandIsValid() {
        when(stockRepository.save(any())).thenReturn(StockMock.available(0));
        CreateStockForNewMaterialCommand command = new CreateStockForNewMaterialCommand(MATERIAL_ID);

        Stock result = useCase.execute(command);

        assertThat(result).isNotNull();
        verify(stockRepository).save(any());
    }
}
