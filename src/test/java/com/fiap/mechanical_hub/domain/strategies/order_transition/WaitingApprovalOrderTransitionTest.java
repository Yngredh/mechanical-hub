package com.fiap.mechanical_hub.domain.strategies.order_transition;

import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;
import com.fiap.mechanical_hub.domain.exceptions.BusinessRuleException;
import com.fiap.mechanical_hub.domain.interfaces.SendBudgetApproval;
import com.fiap.mechanical_hub.mocks.domain.entities.ServiceOrderMock;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class WaitingApprovalOrderTransitionTest {

    private final SendBudgetApproval sendBudgetApproval = mock(SendBudgetApproval.class);
    private final WaitingApprovalOrderTransition transition = new WaitingApprovalOrderTransition(sendBudgetApproval);

    @Test
    void shouldTransitionToWaitingApproval_whenOrderIsInDiagnosis() {
        ServiceOrder order = ServiceOrderMock.inDiagnosis();
        order.updateBudget(BigDecimal.valueOf(500.00));

        transition.execute(order);

        assertThat(order.getStatus()).isEqualTo(OrderStatusEnum.AGUARDANDO_APROVACAO);
    }

    @Test
    void shouldSendBudgetApprovalRequest_whenTransitionSucceeds() {
        ServiceOrder order = ServiceOrderMock.inDiagnosis();
        order.updateBudget(BigDecimal.valueOf(500.00));

        transition.execute(order);

        verify(sendBudgetApproval).sendBudgetApprovalRequest(order);
    }

    @Test
    void shouldThrowException_whenOrderHasNoBudget() {
        ServiceOrder order = ServiceOrderMock.inDiagnosis();

        assertThatThrownBy(() -> transition.execute(order))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("budget");
    }
}
