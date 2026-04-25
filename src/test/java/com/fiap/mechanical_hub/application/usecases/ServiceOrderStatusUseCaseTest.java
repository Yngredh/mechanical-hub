package com.fiap.mechanical_hub.application.usecases;

import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderResponse;
import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderSummaryResponse;
import com.fiap.mechanical_hub.application.mappers.ServiceOrderMapper;
import com.fiap.mechanical_hub.domain.entities.Customer;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;
import com.fiap.mechanical_hub.domain.exceptions.InvalidOrderStatusTransitionException;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.infrastructure.database.repositories.adapter.CustomerRepositoryAdapter;
import com.fiap.mechanical_hub.infrastructure.database.repositories.adapter.OrderTaskRepositoryAdapter;
import com.fiap.mechanical_hub.infrastructure.database.repositories.adapter.ServiceOrderRepositoryAdapter;
import com.fiap.mechanical_hub.infrastructure.database.repositories.adapter.VehicleRepositoryAdapter;
import com.fiap.mechanical_hub.infrastructure.integrations.whatsapp.WhatsAppMessenger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class ServiceOrderStatusUseCaseTest {

    @Mock
    private ServiceOrderRepositoryAdapter serviceOrderRepository;

    @Mock
    private OrderTaskRepositoryAdapter orderTaskRepository;

    @Mock
    private ServiceOrderMapper mapper;

    @Mock
    private CustomerRepositoryAdapter customerRepository;

    @Mock
    private VehicleRepositoryAdapter vehicleRepository;

    @Mock
    private WhatsAppMessenger whatsAppMessenger;

    private ServiceOrderStatusUseCase useCase;
    private UUID orderId;
    private UUID customerId;
    private UUID vehicleId;
    private UUID createdByUserId;

    @BeforeEach
    void setUp() {
        useCase = new ServiceOrderStatusUseCase(serviceOrderRepository, orderTaskRepository, mapper, whatsAppMessenger, customerRepository, vehicleRepository);
        orderId = UUID.randomUUID();
        customerId = UUID.randomUUID();
        vehicleId = UUID.randomUUID();
        createdByUserId = UUID.randomUUID();
    }

    @Test
    void testTransitionToEmDiagnosticoWithMecanicoProfile() {
        // Arrange
        ServiceOrder order = ServiceOrder.create(
                vehicleId,
                customerId,
                "OS-001",
                "Diagnóstico do motor"
        );

        when(serviceOrderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(serviceOrderRepository.save(any())).thenReturn(order);
        when(orderTaskRepository.findByServiceOrderId(orderId)).thenReturn(List.of());
        when(mapper.toResponse(any())).thenReturn(new ServiceOrderResponse());

        // Act
        useCase.updateStatus(orderId, "Em diagnóstico", "Mecânico");

        // Assert
        assertEquals(OrderStatusEnum.EM_DIAGNOSTICO, order.getStatus());
        assertNotNull(order.getOpenedAt());
    }

    @Test
    void testTransitionToEmDiagnosticoWithAdminProfile() {
        // Arrange
        ServiceOrder order = ServiceOrder.create(
                vehicleId,
                customerId,
                "OS-001",
                "Diagnóstico do motor"
        );

        when(serviceOrderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(serviceOrderRepository.save(any())).thenReturn(order);
        when(orderTaskRepository.findByServiceOrderId(orderId)).thenReturn(List.of());
        when(mapper.toResponse(any())).thenReturn(new ServiceOrderResponse());

        // Act
        useCase.updateStatus(orderId, "Em diagnóstico", "Administrador");

        // Assert
        assertEquals(OrderStatusEnum.EM_DIAGNOSTICO, order.getStatus());
    }

    @Test
    void testTransitionToEmDiagnosticoWithClientProfile_ShouldFail() {
        // Arrange
        ServiceOrder order = ServiceOrder.create(
                vehicleId,
                customerId,
                "OS-001",
                "Diagnóstico do motor"
        );

        when(serviceOrderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> useCase.updateStatus(orderId, "Em diagnóstico", "Cliente"));
    }

    @Test
    void testTransitionToEmExecucaoWithStockPending_ShouldFail() {
        // Arrange
        ServiceOrder order = ServiceOrder.create(
                vehicleId,
                customerId,
                "OS-001",
                "Diagnóstico do motor"
        );
        order.startDiagnosis("Mecânico");
        order.updateStockPendingStatus(true);

        when(serviceOrderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> useCase.updateStatus(orderId, "Em execução", "Mecânico"));
    }

    @Test
    void testInvalidTransition() {
        // Arrange
        ServiceOrder order = ServiceOrder.create(
                vehicleId,
                customerId,
                "OS-001",
                "Diagnóstico do motor"
        );

        when(serviceOrderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert - cannot go from CRIADA to EXECUCAO directly
        assertThrows(InvalidOrderStatusTransitionException.class,
                () -> useCase.updateStatus(orderId, "Em execução", "Mecânico"));
    }

    @Test
    void testOrderNotFound() {
        // Arrange
        when(serviceOrderRepository.findById(any())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class,
                () -> useCase.updateStatus(orderId, "Em diagnóstico", "Mecânico"));
    }

    @Test
    void testTimestampsAreSet() {
        // Arrange
        ServiceOrder order = ServiceOrder.create(
                vehicleId,
                customerId,
                "OS-001",
                "Diagnóstico do motor"
        );

        when(serviceOrderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(serviceOrderRepository.save(any())).thenReturn(order);
        when(orderTaskRepository.findByServiceOrderId(orderId)).thenReturn(List.of());
        when(mapper.toResponse(any())).thenReturn(new ServiceOrderResponse());

        // Act
        LocalDateTime beforeUpdate = LocalDateTime.now();
        useCase.updateStatus(orderId, "Em diagnóstico", "Mecânico");
        LocalDateTime afterUpdate = LocalDateTime.now();

        // Assert
        assertNotNull(order.getOpenedAt());
        assertTrue(order.getOpenedAt().isAfter(beforeUpdate) || order.getOpenedAt().isEqual(beforeUpdate));
        assertTrue(order.getOpenedAt().isBefore(afterUpdate) || order.getOpenedAt().isEqual(afterUpdate));
    }

    @Test
    void testApproveOrderInOtherStatus_ShouldFail() {
        // Arrange
        ServiceOrder order = ServiceOrder.create(
                vehicleId,
                customerId,
                "OS-001",
                "Diagnóstico do motor"
        );
        // Status permanece CRIADA (não AGUARDANDO_APROVACAO)

        when(serviceOrderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        InvalidOrderStatusTransitionException exception = assertThrows(
                InvalidOrderStatusTransitionException.class,
                () -> useCase.approve(orderId)
        );
        assertTrue(exception.getMessage().contains("Aprovado"));
    }

    @Test
    void testApproveOrderNotFound_ShouldFail() {
        // Arrange
        when(serviceOrderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> useCase.approve(orderId)
        );
        assertTrue(exception.getMessage().contains(orderId.toString()));
    }

    @Test
    void testFindByCustomerId_CustomerNotFound() {
        // Arrange
        UUID customerId = UUID.randomUUID();
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> useCase.findByCustomerId(customerId)
        );
        assertTrue(exception.getMessage().contains("Customer not found"));
    }

    @Test
    void testFindAll_InvalidDateRange() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = LocalDateTime.now();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.findAll(null, null, startDate, endDate)
        );
        assertTrue(exception.getMessage().contains("Start date cannot be after end date"));
    }

    @Test
    void testFindAll_ValidParameters() {
        // Arrange
        List<ServiceOrderSummaryResponse> expectedResponse = List.of();
        when(serviceOrderRepository.findAllSummaries(any(), any(), any(), any())).thenReturn(expectedResponse);

        // Act
        List<ServiceOrderSummaryResponse> result = useCase.findAll("APROVADO", UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        // Assert
        assertEquals(expectedResponse, result);
        verify(serviceOrderRepository).findAllSummaries(any(), any(), any(), any());
    }

    @Test
    void testFindAll_RepositoryException() {
        // Arrange
        when(serviceOrderRepository.findAllSummaries(any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.findAll(null, null, null, null)
        );
        assertTrue(exception.getMessage().contains("Error retrieving service orders"));
        assertTrue(exception.getCause() instanceof RuntimeException);
    }
}
