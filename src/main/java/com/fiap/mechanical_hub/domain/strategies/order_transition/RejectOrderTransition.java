package com.fiap.mechanical_hub.domain.strategies.order_transition;

import com.fiap.mechanical_hub.application.usecases.stock.RestoreReservedStockItemsUseCase;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;

public class RejectOrderTransition implements OrderStatusTransition {

    private final RestoreReservedStockItemsUseCase restoreReservedStockItemsUseCase;

    public RejectOrderTransition(RestoreReservedStockItemsUseCase restoreReservedStockItemsUseCase) {
        this.restoreReservedStockItemsUseCase = restoreReservedStockItemsUseCase;
    }

    @Override
    public void execute(ServiceOrder order) {
        order.reject();
        restoreReservedStockItemsUseCase.execute(order.getId(), order.getOrderTasks());
    }

}
