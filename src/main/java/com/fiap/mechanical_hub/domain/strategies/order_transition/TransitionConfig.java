package com.fiap.mechanical_hub.domain.strategies.order_transition;

import com.fiap.mechanical_hub.domain.interfaces.SendBudgetApproval;
import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;
import java.util.Map;

public class TransitionConfig {

    public OrderStatusTransitionFactory transitionFactory(SendBudgetApproval sendBudgetApproval) {
        return new OrderStatusTransitionFactory(
                Map.of(
                        OrderStatusEnum.EM_DIAGNOSTICO,
                        new StartDiagnosisTransition(),

                        OrderStatusEnum.AGUARDANDO_APROVACAO,
                        new WaitingApprovalOrderTransition(sendBudgetApproval),

                        OrderStatusEnum.APROVADO,
                        new ApproveOrderTransition(),

                        OrderStatusEnum.RECUSADO,
                        new RejectOrderTransition(),

                        OrderStatusEnum.FINALIZADO,
                        new FinishOrderTransition(),

                        OrderStatusEnum.ENTREGUE,
                        new DeliverOrderTransition()
                )
        );
    }

}