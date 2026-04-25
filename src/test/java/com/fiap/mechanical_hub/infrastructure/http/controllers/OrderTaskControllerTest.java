package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fiap.mechanical_hub.application.dto.ordertask.OrderTaskStatusUpdateRequest;
import com.fiap.mechanical_hub.application.dto.serviceorder.OrderTaskResponse;
import com.fiap.mechanical_hub.application.mappers.OrderTaskMapper;
import com.fiap.mechanical_hub.domain.entities.OrderTask;
import com.fiap.mechanical_hub.domain.enums.TaskStatus;
import com.fiap.mechanical_hub.domain.services.OrderTaskDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderTaskControllerTest {

    @Mock
    private OrderTaskDomainService orderTaskDomainService;

    @Mock
    private OrderTaskMapper orderTaskMapper;

    @InjectMocks
    private OrderTaskController orderTaskController;

    private UUID orderTaskId;
    private OrderTask orderTask;
    private OrderTaskResponse orderTaskResponse;

    @BeforeEach
    void setUp() {
        orderTaskId = UUID.randomUUID();
        orderTask = OrderTask.create(UUID.randomUUID(), UUID.randomUUID());
        orderTaskResponse = new OrderTaskResponse(
                orderTaskId,
                orderTask.getServiceOrderId(),
                orderTask.getServiceId(),
                TaskStatus.INICIADO.getDisplayName(),
                orderTask.getStartedAt(),
                orderTask.getFinishedAt()
        );
    }

    @Test
    void testUpdateStatus_Success() {
        // Arrange
        OrderTaskStatusUpdateRequest request = new OrderTaskStatusUpdateRequest("INICIADO");
        when(orderTaskDomainService.updateStatus(orderTaskId, "INICIADO")).thenReturn(orderTask);
        when(orderTaskMapper.toResponse(orderTask)).thenReturn(orderTaskResponse);

        // Act
        ResponseEntity<OrderTaskResponse> response = orderTaskController.updateStatus(orderTaskId, request);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(orderTaskResponse, response.getBody());
    }
}
