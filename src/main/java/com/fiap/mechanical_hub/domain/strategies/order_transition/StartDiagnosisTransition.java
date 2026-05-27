package com.fiap.mechanical_hub.domain.strategies.order_transition;

import com.fiap.mechanical_hub.domain.entities.ServiceOrder;

public class StartDiagnosisTransition
        implements OrderStatusTransition {

    @Override
    public void execute(ServiceOrder order) {
        order.startDiagnosis();
    }

}