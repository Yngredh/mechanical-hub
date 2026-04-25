package com.fiap.mechanical_hub.domain.entities;

import com.fiap.mechanical_hub.domain.enums.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTaskTest {

    @Test
    void testCreateOrderTask() {
        // Arrange
        UUID serviceOrderId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();

        // Act
        OrderTask task = OrderTask.create(serviceOrderId, serviceId);

        // Assert
        assertNotNull(task.getId());
        assertEquals(serviceOrderId, task.getServiceOrderId());
        assertEquals(serviceId, task.getServiceId());
        assertEquals(TaskStatus.PENDENTE, task.getStatus());
        assertNull(task.getStartedAt());
        assertNull(task.getFinishedAt());
    }

    @Test
    void testStartTaskFromPendingStatus() {
        // Arrange
        OrderTask task = OrderTask.create(UUID.randomUUID(), UUID.randomUUID());

        // Act
        LocalDateTime beforeStart = LocalDateTime.now();
        task.start();
        LocalDateTime afterStart = LocalDateTime.now();

        // Assert
        assertEquals(TaskStatus.INICIADO, task.getStatus());
        assertNotNull(task.getStartedAt());
        assertTrue(task.getStartedAt().isAfter(beforeStart) || task.getStartedAt().isEqual(beforeStart));
        assertTrue(task.getStartedAt().isBefore(afterStart) || task.getStartedAt().isEqual(afterStart));
        assertNull(task.getFinishedAt());
    }

    @Test
    void testStartTaskFromNonPendingStatus_ShouldFail() {
        // Arrange
        OrderTask task = OrderTask.create(UUID.randomUUID(), UUID.randomUUID());
        task.start(); // Já iniciou

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, task::start);
        assertEquals("Tarefa precisa estar em status PENDENTE para ser iniciada", exception.getMessage());
    }

    @Test
    void testApproveTaskFromIniciadoStatus() {
        // Arrange
        OrderTask task = OrderTask.create(UUID.randomUUID(), UUID.randomUUID());
        task.start();

        // Act
        LocalDateTime beforeApprove = LocalDateTime.now();
        task.approve();
        LocalDateTime afterApprove = LocalDateTime.now();

        // Assert
        assertEquals(TaskStatus.APROVADO, task.getStatus());
        assertNotNull(task.getStartedAt());
        // Note: approve() atualiza startedAt novamente, o que pode ser um bug
        assertTrue(task.getStartedAt().isAfter(beforeApprove) || task.getStartedAt().isEqual(beforeApprove));
        assertTrue(task.getStartedAt().isBefore(afterApprove) || task.getStartedAt().isEqual(afterApprove));
        assertNull(task.getFinishedAt());
    }

    @Test
    void testApproveTaskFromPendingStatus_ShouldFail() {
        // Arrange
        OrderTask task = OrderTask.create(UUID.randomUUID(), UUID.randomUUID());
        // Não iniciou a tarefa

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, task::approve);
        assertEquals("Tarefa precisa estar em status INICIADO para ser aprovada", exception.getMessage());
    }

    @Test
    void testApproveTaskFromFinalizadoStatus_ShouldFail() {
        // Arrange
        OrderTask task = OrderTask.create(UUID.randomUUID(), UUID.randomUUID());
        task.start();
        task.finish(); // Finalizou

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, task::approve);
        assertEquals("Tarefa precisa estar em status INICIADO para ser aprovada", exception.getMessage());
    }

    @Test
    void testRefuseTaskFromIniciadoStatus() {
        // Arrange
        OrderTask task = OrderTask.create(UUID.randomUUID(), UUID.randomUUID());
        task.start();

        // Act
        LocalDateTime beforeRefuse = LocalDateTime.now();
        task.refuse();
        LocalDateTime afterRefuse = LocalDateTime.now();

        // Assert
        assertEquals(TaskStatus.RECUSADO, task.getStatus());
        assertNotNull(task.getStartedAt());
        // Note: refuse() atualiza startedAt novamente, o que pode ser um bug
        assertTrue(task.getStartedAt().isAfter(beforeRefuse) || task.getStartedAt().isEqual(beforeRefuse));
        assertTrue(task.getStartedAt().isBefore(afterRefuse) || task.getStartedAt().isEqual(afterRefuse));
        assertNull(task.getFinishedAt());
    }

    @Test
    void testRefuseTaskFromPendingStatus_ShouldFail() {
        // Arrange
        OrderTask task = OrderTask.create(UUID.randomUUID(), UUID.randomUUID());
        // Não iniciou a tarefa

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, task::refuse);
        assertEquals("Tarefa precisa estar em status INICIADO para ser recusada", exception.getMessage());
    }

    @Test
    void testRefuseTaskFromFinalizadoStatus_ShouldFail() {
        // Arrange
        OrderTask task = OrderTask.create(UUID.randomUUID(), UUID.randomUUID());
        task.start();
        task.finish(); // Finalizou

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, task::refuse);
        assertEquals("Tarefa precisa estar em status INICIADO para ser recusada", exception.getMessage());
    }

    @Test
    void testFinishTaskFromIniciadoStatus() {
        // Arrange
        OrderTask task = OrderTask.create(UUID.randomUUID(), UUID.randomUUID());
        task.start();

        // Act
        LocalDateTime beforeFinish = LocalDateTime.now();
        task.finish();
        LocalDateTime afterFinish = LocalDateTime.now();

        // Assert
        assertEquals(TaskStatus.FINALIZADO, task.getStatus());
        assertNotNull(task.getStartedAt());
        assertNotNull(task.getFinishedAt());
        assertTrue(task.getFinishedAt().isAfter(beforeFinish) || task.getFinishedAt().isEqual(beforeFinish));
        assertTrue(task.getFinishedAt().isBefore(afterFinish) || task.getFinishedAt().isEqual(afterFinish));
    }

    @Test
    void testFinishTaskFromPendingStatus_ShouldFail() {
        // Arrange
        OrderTask task = OrderTask.create(UUID.randomUUID(), UUID.randomUUID());
        // Não iniciou a tarefa

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, task::finish);
        assertEquals("Tarefa precisa estar em status INICIADO para ser finalizada", exception.getMessage());
    }

    @Test
    void testFinishTaskFromAprovadoStatus_ShouldFail() {
        // Arrange
        OrderTask task = OrderTask.create(UUID.randomUUID(), UUID.randomUUID());
        task.start();
        task.approve(); // Aprovou

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, task::finish);
        assertEquals("Tarefa precisa estar em status INICIADO para ser finalizada", exception.getMessage());
    }

    @Test
    void testIsFinishedReturnsTrueForFinalizadoStatus() {
        // Arrange
        OrderTask task = OrderTask.create(UUID.randomUUID(), UUID.randomUUID());
        task.start();
        task.finish();

        // Act & Assert
        assertTrue(task.isFinished());
    }

    @Test
    void testApproveAfterRefuse_ShouldFail() {
        // Arrange
        OrderTask task = OrderTask.create(UUID.randomUUID(), UUID.randomUUID());
        task.start();
        task.refuse(); // Já recusou

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, task::approve);
        assertEquals("Tarefa precisa estar em status INICIADO para ser aprovada", exception.getMessage());
    }

    @Test
    void testRefuseAfterApprove_ShouldFail() {
        // Arrange
        OrderTask task = OrderTask.create(UUID.randomUUID(), UUID.randomUUID());
        task.start();
        task.approve(); // Já aprovou

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, task::refuse);
        assertEquals("Tarefa precisa estar em status INICIADO para ser recusada", exception.getMessage());
    }
}
