package com.fiap.mechanical_hub.domain.strategies.order_transition;

import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;
import com.fiap.mechanical_hub.domain.interfaces.SendBudgetApproval;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class TransitionConfigTest {

    private final SendBudgetApproval sendBudgetApproval = mock(SendBudgetApproval.class);
    private final TransitionConfig config = new TransitionConfig();

    @Test
    void shouldReturnFactory_withAllSupportedTransitions() {
        OrderStatusTransitionFactory factory = config.transitionFactory(sendBudgetApproval);

        assertThat(factory.get(OrderStatusEnum.EM_DIAGNOSTICO)).isInstanceOf(StartDiagnosisTransition.class);
        assertThat(factory.get(OrderStatusEnum.AGUARDANDO_APROVACAO)).isInstanceOf(WaitingApprovalOrderTransition.class);
        assertThat(factory.get(OrderStatusEnum.APROVADO)).isInstanceOf(ApproveOrderTransition.class);
        assertThat(factory.get(OrderStatusEnum.RECUSADO)).isInstanceOf(RejectOrderTransition.class);
        assertThat(factory.get(OrderStatusEnum.FINALIZADO)).isInstanceOf(FinishOrderTransition.class);
        assertThat(factory.get(OrderStatusEnum.ENTREGUE)).isInstanceOf(DeliverOrderTransition.class);
    }

    @Test
    void shouldThrowException_whenRequestingUnsupportedTransition() {
        OrderStatusTransitionFactory factory = config.transitionFactory(sendBudgetApproval);

        assertThat(factory).isNotNull();
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> factory.get(OrderStatusEnum.RECEBIDO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported transition");
    }
}
