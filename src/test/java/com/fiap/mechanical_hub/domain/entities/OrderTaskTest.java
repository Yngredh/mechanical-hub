package com.fiap.mechanical_hub.domain.entities;

import com.fiap.mechanical_hub.domain.entities.mocks.OrderTaskMock;
import com.fiap.mechanical_hub.domain.entities.mocks.ServiceMock;
import com.fiap.mechanical_hub.domain.entities.constants.TestConstants;
import com.fiap.mechanical_hub.domain.enums.TaskStatusEnum;
import com.fiap.mechanical_hub.domain.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderTaskTest {

    @Test
    void shouldCreateOrderTaskWithValidData() {
        OrderTask task = OrderTaskMock.defaultOrderTask();

        assertNotNull(task.getId());
        assertEquals(TestConstants.DEFAULT_SERVICE_ORDER_ID, task.getServiceOrderId());
        assertNotNull(task.getServiceData());
        assertEquals(TaskStatusEnum.PENDENTE, task.getStatus());
        assertNull(task.getStartedAt());
        assertNull(task.getFinishedAt());
    }

    @Test
    void shouldStartTaskFromPendingStatus() {
        OrderTask task = OrderTaskMock.defaultOrderTask();
        assertEquals(TaskStatusEnum.PENDENTE, task.getStatus());

        task.start();

        assertEquals(TaskStatusEnum.INICIADO, task.getStatus());
        assertNotNull(task.getStartedAt());
    }

    @Test
    void shouldFinishTaskFromStartedStatus() {
        OrderTask task = OrderTaskMock.defaultOrderTask();
        task.start();
        assertEquals(TaskStatusEnum.INICIADO, task.getStatus());

        task.finish();

        assertEquals(TaskStatusEnum.FINALIZADO, task.getStatus());
        assertNotNull(task.getFinishedAt());
    }

    @Test
    void shouldThrowExceptionWhenStartingTaskNotInPendingStatus() {
        OrderTask task = OrderTaskMock.defaultOrderTask();
        task.start();

        assertThrows(BusinessRuleException.class, task::start);
    }

    @Test
    void shouldThrowExceptionWhenFinishingTaskNotInStartedStatus() {
        OrderTask task = OrderTaskMock.defaultOrderTask();

        assertThrows(BusinessRuleException.class, task::finish);
    }

    @Test
    void shouldThrowExceptionWhenFinishingAlreadyFinishedTask() {
        OrderTask task = OrderTaskMock.defaultOrderTask();
        task.start();
        task.finish();

        assertThrows(BusinessRuleException.class, task::finish);
    }

    @Test
    void shouldIndicateTaskIsFinished() {
        OrderTask task = OrderTaskMock.defaultOrderTask();
        assertFalse(task.isFinished());

        task.start();
        assertFalse(task.isFinished());

        task.finish();
        assertTrue(task.isFinished());
    }

    @Test
    void shouldIndicateTaskIsNotFinishedWhenPending() {
        OrderTask task = OrderTaskMock.defaultOrderTask();

        assertFalse(task.isFinished());
    }

    @Test
    void shouldIndicateTaskIsNotFinishedWhenStarted() {
        OrderTask task = OrderTaskMock.defaultOrderTask();
        task.start();

        assertFalse(task.isFinished());
    }

    @Test
    void shouldTransitionThroughAllStatusesCorrectly() {
        OrderTask task = OrderTaskMock.defaultOrderTask();

        assertEquals(TaskStatusEnum.PENDENTE, task.getStatus());
        task.start();
        assertEquals(TaskStatusEnum.INICIADO, task.getStatus());
        task.finish();
        assertEquals(TaskStatusEnum.FINALIZADO, task.getStatus());
    }

    @Test
    void shouldCreateTaskWithCustomService() {
        ServiceData customService = ServiceMock.serviceWithHighPrice();
        OrderTask task = OrderTaskMock.orderTaskWithCustomService(customService);

        assertEquals(customService.getId(), task.getServiceData().getId());
        assertEquals(customService.getName(), task.getServiceData().getName());
    }

}

