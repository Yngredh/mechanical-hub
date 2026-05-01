package com.fiap.mechanical_hub.domain.strategies.order_transition.mocks;

import com.fiap.mechanical_hub.domain.entities.OrderTask;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.fiap.mechanical_hub.domain.strategies.order_transition.constants.TestConstants.*;

public final class ServiceOrderMock {

    private ServiceOrderMock() {
    }

    public static final String DEFAULT_ORDER_NUMBER = "202604-0001";
    public static final String DEFAULT_REQUEST_DESCRIPTION = "Troca de óleo e revisão geral";
    public static final UUID DEFAULT_VEHICLE_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    public static final UUID DEFAULT_CUSTOMER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    public static final UUID DEFAULT_CREATED_BY_USER_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    public static ServiceOrder receivedOrder() {
        return ServiceOrder.create(
                DEFAULT_VEHICLE_ID,
                DEFAULT_CUSTOMER_ID,
                DEFAULT_ORDER_NUMBER,
                DEFAULT_REQUEST_DESCRIPTION,
                DEFAULT_CREATED_BY_USER_ID
        );
    }

    public static ServiceOrder inDiagnosisOrderWithBudget(BigDecimal budget) {
        ServiceOrder order = receivedOrder();
        order.startDiagnosis();
        order.updateBudget(budget);
        return order;
    }

    public static ServiceOrder inDiagnosisOrderWithoutBudget() {
        ServiceOrder order = receivedOrder();
        order.startDiagnosis();
        return order;
    }

    public static ServiceOrder waitingApprovalOrder() {
        ServiceOrder order = inDiagnosisOrderWithBudget(DEFAULT_BUDGET);
        order.submitForApproval();
        return order;
    }

    public static ServiceOrder approvedOrder(boolean hasStockPending) {
        ServiceOrder order = waitingApprovalOrder();
        order.approve();
        order.setHasStockPending(hasStockPending);
        return order;
    }

    public static ServiceOrder inExecutionOrderWithTasks(List<OrderTask> tasks) {
        ServiceOrder order = approvedOrder(false);

        // move to execution using domain behavior
        order.startExecution();

        tasks.forEach(order::addTask);
        return order;
    }

    public static ServiceOrder finishedOrder() {
        ServiceOrder order = inExecutionOrderWithTasks(List.of(
                OrderTaskMock.finishedTask(receivedOrder().getId(), DEFAULT_SERVICE_ID)
        ));
        // ensure tasks belong to order id
        order.getOrderTasks().clear();
        order.addTask(OrderTaskMock.finishedTask(order.getId(), DEFAULT_SERVICE_ID));
        order.finish();
        return order;
    }

    public static ServiceOrder orderWithStatus(OrderStatusEnum status) {
        ServiceOrder order = receivedOrder();
        order.setStatus(status);
        return order;
    }
}

