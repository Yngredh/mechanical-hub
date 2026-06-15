package com.fiap.mechanical_hub.application.usecases.serviceorder;

import com.fiap.mechanical_hub.application.command.customer.FindOrCreateCustomerCommand;
import com.fiap.mechanical_hub.application.usecases.customer.FindOrCreateCustomerUseCase;
import com.fiap.mechanical_hub.domain.entities.Customer;
import com.fiap.mechanical_hub.mocks.domain.entities.CustomerMock;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FindOrCreateServiceOrderCustomerUseCaseTest {

    private final FindOrCreateCustomerUseCase findOrCreateCustomerUseCase = mock(FindOrCreateCustomerUseCase.class);
    private final FindOrCreateServiceOrderCustomerUseCase useCase = new FindOrCreateServiceOrderCustomerUseCase(findOrCreateCustomerUseCase);

    @Test
    void shouldReturnCustomer_whenDelegatingToFindOrCreateCustomerUseCase() {
        when(findOrCreateCustomerUseCase.execute(any(FindOrCreateCustomerCommand.class)))
                .thenReturn(CustomerMock.withDefaultValues());

        Customer result = useCase.execute("João Silva", "CPF", "12345678901", "11987654321", "joao@email.com", "Rua A, 1");

        assertThat(result).isNotNull();
        verify(findOrCreateCustomerUseCase).execute(any(FindOrCreateCustomerCommand.class));
    }
}
