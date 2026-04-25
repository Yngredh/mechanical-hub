package com.fiap.mechanical_hub.domain.services;

import com.fiap.mechanical_hub.domain.entities.OrderTask;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.enums.OrderStatus;
import com.fiap.mechanical_hub.domain.enums.TaskStatus;
import com.fiap.mechanical_hub.domain.exceptions.InvalidOrderStatusTransitionException;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.OrderTaskRepository;
import com.fiap.mechanical_hub.domain.repositories.ServiceOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderTaskDomainServiceTest {

    @Mock
    private OrderTaskRepository orderTaskRepository;

    @Mock
    private ServiceOrderRepository serviceOrderRepository;

    @InjectMocks
    private OrderTaskDomainService orderTaskDomainService;

    private UUID orderTaskId;
    private UUID serviceOrderId;
    private OrderTask orderTask;
    private ServiceOrder serviceOrder;

    @BeforeEach
    void setUp() {
        orderTaskId = UUID.randomUUID();
        serviceOrderId = UUID.randomUUID();

        orderTask = OrderTask.create(serviceOrderId, UUID.randomUUID());
        serviceOrder = new ServiceOrder();
        serviceOrder.setId(serviceOrderId);
        serviceOrder.setStatus(OrderStatus.APROVADO);
    }

    @Test
    void testUpdateStatusToIniciado_Success() {
        // Arrange
        when(orderTaskRepository.findById(orderTaskId)).thenReturn(Optional.of(orderTask));
        when(serviceOrderRepository.findById(serviceOrderId)).thenReturn(Optional.of(serviceOrder));
        when(orderTaskRepository.save(any(OrderTask.class))).thenReturn(orderTask);
        when(serviceOrderRepository.save(any(ServiceOrder.class))).thenReturn(serviceOrder);

        // Act
        OrderTask result = orderTaskDomainService.updateStatus(orderTaskId, "INICIADO");

        // Assert
        assertEquals(TaskStatus.INICIADO, result.getStatus());
        assertNotNull(result.getStartedAt());
        verify(orderTaskRepository).save(orderTask);
        verify(serviceOrderRepository).save(serviceOrder); // Should update service order to EM_EXECUCAO
    }

    @Test
    void testUpdateStatusToFinalizado_Success() {
        // Arrange
        orderTask.start(); // Set to INICIADO first
        when(orderTaskRepository.findById(orderTaskId)).thenReturn(Optional.of(orderTask));
        when(orderTaskRepository.save(any(OrderTask.class))).thenReturn(orderTask);

        // Act
        OrderTask result = orderTaskDomainService.updateStatus(orderTaskId, "FINALIZADO");

        // Assert
        assertEquals(TaskStatus.FINALIZADO, result.getStatus());
        assertNotNull(result.getFinishedAt());
        verify(orderTaskRepository).save(orderTask);
    }

    @Test
    void testUpdateStatus_InvalidTransition_FromPendenteToFinalizado() {
        // Arrange
        when(orderTaskRepository.findById(orderTaskId)).thenReturn(Optional.of(orderTask));

        // Act & Assert
        InvalidOrderStatusTransitionException exception = assertThrows(
                InvalidOrderStatusTransitionException.class,
                () -> orderTaskDomainService.updateStatus(orderTaskId, "FINALIZADO")
        );

        assertTrue(exception.getMessage().contains("Pendente"));
        assertTrue(exception.getMessage().contains("Finalizado"));
    }

    @Test
    void testUpdateStatus_OrderTaskNotFound() {
        // Arrange
        when(orderTaskRepository.findById(orderTaskId)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> orderTaskDomainService.updateStatus(orderTaskId, "INICIADO")
        );

        assertTrue(exception.getMessage().contains("Order task with id"));
    }

    @Test
    void testUpdateStatus_ServiceOrderNotFound() {
        // Arrange
        when(orderTaskRepository.findById(orderTaskId)).thenReturn(Optional.of(orderTask));
        when(serviceOrderRepository.findById(serviceOrderId)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> orderTaskDomainService.updateStatus(orderTaskId, "INICIADO")
        );

        assertTrue(exception.getMessage().contains("Service order with id"));
    }
}
