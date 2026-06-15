package com.fiap.mechanical_hub.application.usecases.stock;

import com.fiap.mechanical_hub.domain.entities.Stock;
import com.fiap.mechanical_hub.domain.repositories.StockRepository;
import com.fiap.mechanical_hub.mocks.domain.entities.StockMock;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FindAllStocksUseCaseTest {

    private final StockRepository stockRepository = mock(StockRepository.class);
    private final FindAllStocksUseCase useCase = new FindAllStocksUseCase(stockRepository);

    @Test
    void shouldReturnAllStocks_whenStocksExist() {
        when(stockRepository.findAll()).thenReturn(List.of(StockMock.available(10), StockMock.reserved(5)));

        List<Stock> result = useCase.execute();

        assertThat(result).hasSize(2);
    }

    @Test
    void shouldReturnEmptyList_whenNoStocksExist() {
        when(stockRepository.findAll()).thenReturn(List.of());

        List<Stock> result = useCase.execute();

        assertThat(result).isEmpty();
    }
}
