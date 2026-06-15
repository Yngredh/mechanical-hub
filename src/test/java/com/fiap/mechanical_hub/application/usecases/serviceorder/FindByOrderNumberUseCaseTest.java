package com.fiap.mechanical_hub.application.usecases.serviceorder;

import com.fiap.mechanical_hub.application.command.serviceorder.FindByOrderNumberCommand;
import com.fiap.mechanical_hub.application.dto.customer.CustomerResponse;
import com.fiap.mechanical_hub.application.dto.serviceorder.request.ServiceOrderCustomerView;
import com.fiap.mechanical_hub.application.dto.vehicle.VehicleResponse;
import com.fiap.mechanical_hub.application.usecases.customer.FindCustomerByIdUseCase;
import com.fiap.mechanical_hub.application.usecases.vehicle.FindVehicleByIdUseCase;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.ServiceOrderRepository;
import com.fiap.mechanical_hub.mocks.domain.entities.ServiceOrderMock;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FindByOrderNumberUseCaseTest {

    private static final String ORDER_NUMBER = "OS-001";

    private final ServiceOrderRepository repository = mock(ServiceOrderRepository.class);
    private final FindVehicleByIdUseCase findVehicleByIdUseCase = mock(FindVehicleByIdUseCase.class);
    private final FindCustomerByIdUseCase findCustomerByIdUseCase = mock(FindCustomerByIdUseCase.class);
    private final FindByOrderNumberUseCase useCase = new FindByOrderNumberUseCase(repository, findVehicleByIdUseCase, findCustomerByIdUseCase);

    @Test
    void shouldReturnCustomerView_whenOrderNumberExists() {
        when(repository.findByOrderNumber(ORDER_NUMBER)).thenReturn(Optional.of(ServiceOrderMock.received()));
        when(findVehicleByIdUseCase.execute(any())).thenReturn(mock(VehicleResponse.class));
        when(findCustomerByIdUseCase.execute(any())).thenReturn(mock(CustomerResponse.class));

        ServiceOrderCustomerView result = useCase.execute(new FindByOrderNumberCommand(ORDER_NUMBER));

        assertThat(result).isNotNull();
    }

    @Test
    void shouldThrowNotFoundException_whenOrderNumberDoesNotExist() {
        when(repository.findByOrderNumber(ORDER_NUMBER)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new FindByOrderNumberCommand(ORDER_NUMBER)))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(ORDER_NUMBER);
    }
}
