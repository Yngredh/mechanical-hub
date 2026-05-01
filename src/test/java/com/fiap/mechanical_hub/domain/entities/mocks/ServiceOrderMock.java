package com.fiap.mechanical_hub.domain.entities.mocks;

import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.entities.OrderTask;

import static com.fiap.mechanical_hub.domain.entities.constants.TestConstants.*;

public class ServiceOrderMock {

    public static ServiceOrder defaultServiceOrder() {
        return ServiceOrder.create(
                DEFAULT_VEHICLE_ID,
                DEFAULT_CUSTOMER_ID,
                DEFAULT_ORDER_NUMBER,
                DEFAULT_REQUEST_DESCRIPTION,
                DEFAULT_USER_ID
        );
    }

    public static ServiceOrder serviceOrderInDiagnosis() {
        ServiceOrder order = ServiceOrder.create(
                DEFAULT_VEHICLE_ID,
                DEFAULT_CUSTOMER_ID,
                DEFAULT_ORDER_NUMBER,
                DEFAULT_REQUEST_DESCRIPTION,
                DEFAULT_USER_ID
        );
        order.startDiagnosis();
        return order;
    }

    public static ServiceOrder serviceOrderAwaitingApproval() {
        ServiceOrder order = ServiceOrder.create(
                DEFAULT_VEHICLE_ID,
                DEFAULT_CUSTOMER_ID,
                DEFAULT_ORDER_NUMBER,
                DEFAULT_REQUEST_DESCRIPTION,
                DEFAULT_USER_ID
        );
        order.startDiagnosis();
        order.updateBudget(DEFAULT_ORDER_BUDGET);
        order.submitForApproval();
        return order;
    }

    public static ServiceOrder serviceOrderApproved() {
        ServiceOrder order = serviceOrderAwaitingApproval();
        order.approve();
        return order;
    }

    public static ServiceOrder serviceOrderInExecution() {
        ServiceOrder order = serviceOrderApproved();
        order.startExecution();
        return order;
    }

    public static ServiceOrder serviceOrderFinalized() {
        ServiceOrder order = serviceOrderInExecution();
        OrderTask task = OrderTaskMock.orderTaskWithCustomValues(order.getId(), ServiceMock.defaultService());
        order.addTask(task);
        task.start();
        task.finish();
        order.finish();
        return order;
    }

    public static ServiceOrder serviceOrderWithStockPending() {
        ServiceOrder order = serviceOrderApproved();
        order.setHasStockPending(true);
        return order;
    }

}

