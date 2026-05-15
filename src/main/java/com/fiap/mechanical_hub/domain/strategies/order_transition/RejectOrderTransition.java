package com.fiap.mechanical_hub.domain.strategies.order_transition;

import com.fiap.mechanical_hub.application.usecases.StockUseCase;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;

import java.util.UUID;

public class RejectOrderTransition implements OrderStatusTransition {

    private final StockUseCase stockUseCase;

    public RejectOrderTransition(StockUseCase stockUseCase) {
        this.stockUseCase = stockUseCase;
    }

    @Override
    public void execute(ServiceOrder order) {
        order.reject();
        stockUseCase.restoreReservedItems(order);
    }

}
