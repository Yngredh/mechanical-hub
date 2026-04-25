package com.fiap.mechanical_hub.application.usecases;

import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderResponse;
import com.fiap.mechanical_hub.application.mappers.ServiceOrderMapper;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.enums.OrderStatus;
import com.fiap.mechanical_hub.domain.exceptions.InvalidOrderStatusTransitionException;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.infrastructure.database.repositories.ServiceOrderJpaRepository;
import com.fiap.mechanical_hub.infrastructure.database.repositories.VehicleRepositoryAdapter;
import com.fiap.mechanical_hub.infrastructure.database.repositories.adapter.CustomerRepositoryAdapter;
import com.fiap.mechanical_hub.infrastructure.database.repositories.adapter.OrderTaskRepositoryAdapter;
import com.fiap.mechanical_hub.infrastructure.database.repositories.adapter.ServiceOrderRepositoryAdapter;
import com.fiap.mechanical_hub.infrastructure.integrations.whatsapp.WhatsAppMessenger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ServiceOrderStatusUseCaseTest {

    @Mock
    private ServiceOrderRepositoryAdapter serviceOrderRepository;

    @Mock
    private OrderTaskRepositoryAdapter orderTaskRepository;

    @Mock
    private ServiceOrderMapper mapper;

    @Mock
    private ServiceOrderJpaRepository serviceOrderJpaRepository;

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
        useCase = new ServiceOrderStatusUseCase(serviceOrderRepository, orderTaskRepository, mapper, whatsAppMessenger,serviceOrderJpaRepository, customerRepository, vehicleRepository);
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
                createdByUserId,
                "OS-001",
                "Diagnóstico do motor",
                BigDecimal.valueOf(500),
                LocalDateTime.now().plusDays(7)
        );

        when(serviceOrderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(serviceOrderRepository.save(any())).thenReturn(order);
        when(orderTaskRepository.findByServiceOrderId(orderId)).thenReturn(List.of());
        when(mapper.toResponse(any())).thenReturn(new ServiceOrderResponse());

        // Act
        useCase.updateStatus(orderId, "Em diagnóstico", "Mecânico");

        // Assert
        assertEquals(OrderStatus.EM_DIAGNOSTICO, order.getStatus());
        assertNotNull(order.getOpenedAt());
    }

    @Test
    void testTransitionToEmDiagnosticoWithAdminProfile() {
        // Arrange
        ServiceOrder order = ServiceOrder.create(
                vehicleId,
                customerId,
                createdByUserId,
                "OS-002",
                "Diagnóstico",
                BigDecimal.valueOf(500),
                LocalDateTime.now().plusDays(7)
        );

        when(serviceOrderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(serviceOrderRepository.save(any())).thenReturn(order);
        when(orderTaskRepository.findByServiceOrderId(orderId)).thenReturn(List.of());
        when(mapper.toResponse(any())).thenReturn(new ServiceOrderResponse());

        // Act
        useCase.updateStatus(orderId, "Em diagnóstico", "Administrador");

        // Assert
        assertEquals(OrderStatus.EM_DIAGNOSTICO, order.getStatus());
    }

    @Test
    void testTransitionToEmDiagnosticoWithClientProfile_ShouldFail() {
        // Arrange
        ServiceOrder order = ServiceOrder.create(
                vehicleId,
                customerId,
                createdByUserId,
                "OS-003",
                "Diagnóstico",
                BigDecimal.valueOf(500),
                LocalDateTime.now().plusDays(7)
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
                createdByUserId,
                "OS-004",
                "Diagnóstico",
                BigDecimal.valueOf(500),
                LocalDateTime.now().plusDays(7)
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
                createdByUserId,
                "OS-007",
                "Diagnóstico",
                BigDecimal.valueOf(500),
                LocalDateTime.now().plusDays(7)
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
                createdByUserId,
                "OS-008",
                "Diagnóstico",
                BigDecimal.valueOf(500),
                LocalDateTime.now().plusDays(7)
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
                createdByUserId,
                "OS-002",
                "Tentativa de aprovação em status errado",
                BigDecimal.valueOf(500),
                LocalDateTime.now().plusDays(7)
        );
        // Status permanece CRIADA (não AGUARDANDO_APROVACAO)

        when(serviceOrderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        InvalidOrderStatusTransitionException exception = assertThrows(
                InvalidOrderStatusTransitionException.class,
                () -> useCase.approve(orderId)
        );
        assertTrue(exception.getMessage().contains("Criado"));
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

}

