package com.fiap.mechanical_hub.application.usecases.serviceorder;

import com.fiap.mechanical_hub.application.dto.customer.CustomerResponse;
import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderDetailResponse;
import com.fiap.mechanical_hub.application.dto.vehicle.VehicleResponse;
import com.fiap.mechanical_hub.application.usecases.customer.FindCustomerByIdUseCase;
import com.fiap.mechanical_hub.application.usecases.vehicle.FindVehicleByIdUseCase;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.ServiceOrderRepository;
import com.fiap.mechanical_hub.mocks.domain.entities.ServiceOrderMock;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FindServiceOrderByIdUseCaseTest {

    private static final UUID ORDER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private final ServiceOrderRepository repository = mock(ServiceOrderRepository.class);
    private final FindVehicleByIdUseCase findVehicleByIdUseCase = mock(FindVehicleByIdUseCase.class);
    private final FindCustomerByIdUseCase findCustomerByIdUseCase = mock(FindCustomerByIdUseCase.class);
    private final FindServiceOrderByIdUseCase useCase = new FindServiceOrderByIdUseCase(repository, findVehicleByIdUseCase, findCustomerByIdUseCase);

    @Test
    void shouldReturnDetailResponse_whenOrderExists() {
        when(repository.findById(ORDER_ID)).thenReturn(Optional.of(ServiceOrderMock.received()));
        when(findVehicleByIdUseCase.execute(any())).thenReturn(mock(VehicleResponse.class));
        when(findCustomerByIdUseCase.execute(any())).thenReturn(mock(CustomerResponse.class));

        ServiceOrderDetailResponse result = useCase.execute(ORDER_ID);

        assertThat(result).isNotNull();
    }

    @Test
    void shouldThrowNotFoundException_whenOrderDoesNotExist() {
        when(repository.findById(ORDER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(ORDER_ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(ORDER_ID.toString());
    }
}
