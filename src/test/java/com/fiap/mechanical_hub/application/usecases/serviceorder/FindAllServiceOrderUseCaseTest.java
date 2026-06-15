package com.fiap.mechanical_hub.application.usecases.serviceorder;

import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderSummaryResponse;
import com.fiap.mechanical_hub.domain.repositories.ServiceOrderRepository;
import com.fiap.mechanical_hub.mocks.domain.entities.ServiceOrderMock;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FindAllServiceOrderUseCaseTest {

    private final ServiceOrderRepository repository = mock(ServiceOrderRepository.class);
    private final FindAllServiceOrderUseCase useCase = new FindAllServiceOrderUseCase(repository);

    @Test
    void shouldReturnSummaryList_whenOrdersExist() {
        when(repository.findAllActiveOrdersWithPriority()).thenReturn(List.of(ServiceOrderMock.received(), ServiceOrderMock.inDiagnosis()));

        List<ServiceOrderSummaryResponse> result = useCase.execute();

        assertThat(result).hasSize(2);
    }

    @Test
    void shouldReturnEmptyList_whenNoOrdersExist() {
        when(repository.findAllActiveOrdersWithPriority()).thenReturn(List.of());

        List<ServiceOrderSummaryResponse> result = useCase.execute();

        assertThat(result).isEmpty();
    }
}
