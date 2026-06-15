package com.fiap.mechanical_hub.application.usecases.serviceorder;

import com.fiap.mechanical_hub.application.command.serviceorder.CreateServiceOrderCommand;
import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderResponse;
import com.fiap.mechanical_hub.application.mappers.ServiceOrderMapper;
import com.fiap.mechanical_hub.application.usecases.vehicle.FindOrCreateVehicleUseCase;
import com.fiap.mechanical_hub.domain.repositories.ServiceOrderRepository;
import com.fiap.mechanical_hub.domain.utils.OrderNumberGenerator;
import com.fiap.mechanical_hub.mocks.domain.entities.CustomerMock;
import com.fiap.mechanical_hub.mocks.domain.entities.ServiceOrderMock;
import com.fiap.mechanical_hub.mocks.domain.entities.VehicleMock;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateServiceOrderUseCaseTest {

    private static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000004");

    private final ServiceOrderRepository repository = mock(ServiceOrderRepository.class);
    private final FindOrCreateVehicleUseCase findOrCreateVehicleUseCase = mock(FindOrCreateVehicleUseCase.class);
    private final FindOrCreateServiceOrderCustomerUseCase findOrCreateCustomerUseCase = mock(FindOrCreateServiceOrderCustomerUseCase.class);
    private final OrderNumberGenerator orderNumberGenerator = mock(OrderNumberGenerator.class);
    private final ServiceOrderMapper mapper = mock(ServiceOrderMapper.class);
    private final CreateServiceOrderUseCase useCase = new CreateServiceOrderUseCase(
            repository, findOrCreateVehicleUseCase, findOrCreateCustomerUseCase, orderNumberGenerator, mapper);

    @Test
    void shouldCreateAndReturnOrderResponse_whenCommandIsValid() {
        CreateServiceOrderCommand command = new CreateServiceOrderCommand(
                "João Silva", "CPF", "12345678901", "11987654321", "joao@email.com", "Rua A, 1",
                "ABC1234", "Toyota", "Corolla", 2022, "Prata", "Troca de óleo", USER_ID);
        when(findOrCreateCustomerUseCase.execute(any(), any(), any(), any(), any(), any()))
                .thenReturn(CustomerMock.withDefaultValues());
        when(findOrCreateVehicleUseCase.execute(any(), any(), any(), any(), any(), any()))
                .thenReturn(VehicleMock.withDefaultValues());
        when(orderNumberGenerator.generate()).thenReturn("OS-001");
        when(repository.save(any())).thenReturn(ServiceOrderMock.received());
        when(mapper.toResponse(any())).thenReturn(mock(ServiceOrderResponse.class));

        ServiceOrderResponse result = useCase.execute(command);

        assertThat(result).isNotNull();
        verify(repository).save(any());
    }

    @Test
    void shouldGenerateOrderNumber_whenCreatingServiceOrder() {
        CreateServiceOrderCommand command = new CreateServiceOrderCommand(
                "João Silva", "CPF", "12345678901", "11987654321", "joao@email.com", "Rua A, 1",
                "ABC1234", "Toyota", "Corolla", 2022, "Prata", "Troca de óleo", USER_ID);
        when(findOrCreateCustomerUseCase.execute(any(), any(), any(), any(), any(), any()))
                .thenReturn(CustomerMock.withDefaultValues());
        when(findOrCreateVehicleUseCase.execute(any(), any(), any(), any(), any(), any()))
                .thenReturn(VehicleMock.withDefaultValues());
        when(orderNumberGenerator.generate()).thenReturn("OS-001");
        when(repository.save(any())).thenReturn(ServiceOrderMock.received());
        when(mapper.toResponse(any())).thenReturn(mock(ServiceOrderResponse.class));

        useCase.execute(command);

        verify(orderNumberGenerator).generate();
    }
}
