package com.fiap.mechanical_hub.mocks.domain.entities;

import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ServiceOrderMock {

    private static final UUID ORDER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID VEHICLE_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID CUSTOMER_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");
    private static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000004");

    public static ServiceOrder received() {
        ServiceOrder order = new ServiceOrder();
        order.setId(ORDER_ID);
        order.setVehicleId(VEHICLE_ID);
        order.setCustomerId(CUSTOMER_ID);
        order.setStatus(OrderStatusEnum.RECEBIDO);
        order.setCreatedByUserId(USER_ID);
        order.setOrderNumber("OS-001");
        order.setRequestDescription("Diagnóstico e reparo");
        order.setBudget(null);
        order.setHasStockPending(false);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        return order;
    }

    public static ServiceOrder inDiagnosis() {
        ServiceOrder order = received();
        order.setStatus(OrderStatusEnum.EM_DIAGNOSTICO);
        order.setOpenedAt(LocalDateTime.now());
        return order;
    }

    public static ServiceOrder waitingApproval() {
        ServiceOrder order = inDiagnosis();
        order.setStatus(OrderStatusEnum.AGUARDANDO_APROVACAO);
        order.setBudget(BigDecimal.valueOf(500.00));
        return order;
    }

    public static ServiceOrder approvedWithoutStockPending() {
        ServiceOrder order = waitingApproval();
        order.setStatus(OrderStatusEnum.APROVADO);
        order.setHasStockPending(false);
        return order;
    }

    public static ServiceOrder approvedWithStockPending() {
        ServiceOrder order = waitingApproval();
        order.setStatus(OrderStatusEnum.APROVADO);
        order.setHasStockPending(true);
        return order;
    }

    public static ServiceOrder inProgress() {
        ServiceOrder order = approvedWithoutStockPending();
        order.setStatus(OrderStatusEnum.EM_EXECUCAO);
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

    public static ServiceOrder rejected() {
        ServiceOrder order = waitingApproval();
        order.setStatus(OrderStatusEnum.RECUSADO);
        return order;
    }

    public static ServiceOrder finished() {
        ServiceOrder order = inProgress();
        order.setStatus(OrderStatusEnum.FINALIZADO);
        order.setCompletedAt(LocalDateTime.now());
        return order;
    }

    public static ServiceOrder delivered() {
        ServiceOrder order = finished();
        order.setStatus(OrderStatusEnum.ENTREGUE);
        order.setDeliveredAt(LocalDateTime.now());
        return order;
    }
}

