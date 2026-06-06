package com.fiap.mechanical_hub.mocks.domain.entities;

import com.fiap.mechanical_hub.domain.entities.ServiceOrder;

import java.math.BigDecimal;
import java.util.UUID;

public class ServiceOrderMock {

    private static final UUID VEHICLE_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID CUSTOMER_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");
    private static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000004");
    private static final String ORDER_NUMBER = "OS-001";
    private static final String REQUEST_DESCRIPTION = "Diagnóstico e reparo";
    private static final BigDecimal BUDGET = BigDecimal.valueOf(500.00);

    public static ServiceOrder received() {
        return ServiceOrder.create(VEHICLE_ID, CUSTOMER_ID, ORDER_NUMBER, REQUEST_DESCRIPTION, USER_ID);
    }

    public static ServiceOrder inDiagnosis() {
        ServiceOrder order = received();
        order.startDiagnosis();
        return order;
    }

    public static ServiceOrder waitingApproval() {
        ServiceOrder order = inDiagnosis();
        order.updateBudget(BUDGET);
        order.submitForApproval();
        return order;
    }

    public static ServiceOrder approvedWithoutStockPending() {
        ServiceOrder order = waitingApproval();
        order.approve();
        return order;
    }

    public static ServiceOrder approvedWithStockPending() {
        ServiceOrder order = waitingApproval();
        order.approve();
        order.setHasStockPending(true);
        return order;
    }

    public static ServiceOrder rejected() {
        ServiceOrder order = waitingApproval();
        order.reject();
        return order;
    }

    public static ServiceOrder inProgress() {
        ServiceOrder order = approvedWithoutStockPending();
        order.startExecution();
        return order;
    }

    public static ServiceOrder withOneUnfinishedTask() {
        ServiceOrder order = inProgress();
        order.addTask(OrderTaskMock.notStarted());
        return order;
    }

    public static ServiceOrder withAllTasksFinished() {
        ServiceOrder order = inProgress();
        order.addTask(OrderTaskMock.finished());
        return order;
    }

    public static ServiceOrder finished() {
        ServiceOrder order = withAllTasksFinished();
        order.finish();
        return order;
    }

    public static ServiceOrder delivered() {
        ServiceOrder order = finished();
        order.deliver();
        return order;
    }
}
