package com.fiap.mechanical_hub.domain.strategies.order_transition;

import com.fiap.mechanical_hub.domain.entities.ServiceOrder;

public class RejectOrderTransition implements OrderStatusTransition {


    @Override
    public void execute(ServiceOrder order) {
        order.reject();
    }

}
