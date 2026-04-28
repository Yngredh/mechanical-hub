package com.fiap.mechanical_hub.domain.strategies.order_transition;

import com.fiap.mechanical_hub.application.usecases.StockUseCase;
import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class TransitionConfig {

    @Bean
    public OrderStatusTransitionFactory transitionFactory(StockUseCase stockUseCase) {
        return new OrderStatusTransitionFactory(
                Map.of(
                        OrderStatusEnum.EM_DIAGNOSTICO,
                        new StartDiagnosisTransition(),

                        OrderStatusEnum.APROVADO,
                        new ApproveOrderTransition(),

                        OrderStatusEnum.RECUSADO,
                        new RejectOrderTransition(stockUseCase),

                        OrderStatusEnum.FINALIZADO,
                        new FinishOrderTransition(),

                        OrderStatusEnum.ENTREGUE,
                        new DeliverOrderTransition()
                )
        );
    }

}