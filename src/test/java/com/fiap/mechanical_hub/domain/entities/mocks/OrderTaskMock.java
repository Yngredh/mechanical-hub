package com.fiap.mechanical_hub.domain.entities.mocks;

import com.fiap.mechanical_hub.domain.entities.OrderTask;
import com.fiap.mechanical_hub.domain.entities.ServiceData;

import static com.fiap.mechanical_hub.domain.entities.constants.TestConstants.DEFAULT_SERVICE_ORDER_ID;

public class OrderTaskMock {

    public static OrderTask defaultOrderTask() {
        ServiceData service = ServiceMock.defaultService();
        return OrderTask.create(DEFAULT_SERVICE_ORDER_ID, service);
    }

    public static OrderTask orderTaskWithCustomService(ServiceData service) {
        return OrderTask.create(DEFAULT_SERVICE_ORDER_ID, service);
    }

    public static OrderTask orderTaskWithCustomValues(java.util.UUID serviceOrderId, ServiceData service) {
        return OrderTask.create(serviceOrderId, service);
    }

}

