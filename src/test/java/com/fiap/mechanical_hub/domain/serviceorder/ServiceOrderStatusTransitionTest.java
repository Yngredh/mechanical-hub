package com.fiap.mechanical_hub.domain.serviceorder;

import com.fiap.mechanical_hub.mocks.domain.entities.ServiceOrderMock;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;
import com.fiap.mechanical_hub.domain.exceptions.InvalidOrderTransitionException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ServiceOrderStatusTransitionTest {

    @Test
    void shouldTransitionToInDiagnosis_whenOrderIsReceived() {
        ServiceOrder order = ServiceOrderMock.received();

        order.startDiagnosis();

        assertThat(order.getStatus()).isEqualTo(OrderStatusEnum.EM_DIAGNOSTICO);
        assertThat(order.getOpenedAt()).isNotNull();
    }

    @Test
    void shouldThrowException_whenTransitioningFromReceivedToApproved() {
        ServiceOrder order = ServiceOrderMock.received();

        assertThatThrownBy(order::approve)
                .isInstanceOf(InvalidOrderTransitionException.class)
                .hasMessageContaining("Invalid transition");
    }

    @Test
    void shouldTransitionToWaitingApproval_whenOrderIsInDiagnosis() {
        ServiceOrder order = ServiceOrderMock.inDiagnosis();
        order.setBudget(java.math.BigDecimal.valueOf(500.00));

        order.submitForApproval();

        assertThat(order.getStatus()).isEqualTo(OrderStatusEnum.AGUARDANDO_APROVACAO);
    }

    @Test
    void shouldThrowException_whenSubmittingForApprovalWithoutBudget() {
        ServiceOrder order = ServiceOrderMock.inDiagnosis();

        assertThatThrownBy(order::submitForApproval)
                .isInstanceOf(com.fiap.mechanical_hub.domain.exceptions.BusinessRuleException.class)
                .hasMessageContaining("budget");
    }

    @Test
    void shouldTransitionToApproved_whenOrderIsWaitingApproval() {
        ServiceOrder order = ServiceOrderMock.waitingApproval();

        order.approve();

        assertThat(order.getStatus()).isEqualTo(OrderStatusEnum.APROVADO);
    }

    @Test
    void shouldTransitionToRejected_whenOrderIsWaitingApproval() {
        ServiceOrder order = ServiceOrderMock.waitingApproval();

        order.reject();

        assertThat(order.getStatus()).isEqualTo(OrderStatusEnum.RECUSADO);
    }

    @Test
    void shouldThrowException_whenTransitioningFromApprovedToFinished() {
        ServiceOrder order = ServiceOrderMock.approvedWithoutStockPending();

        assertThatThrownBy(order::finish)
                .isInstanceOf(InvalidOrderTransitionException.class)
                .hasMessageContaining("Invalid transition");
    }

    @Test
    void shouldTransitionToInProgress_whenOrderIsApprovedAndHasNoStockPending() {
        ServiceOrder order = ServiceOrderMock.approvedWithoutStockPending();

        order.startExecution();

        assertThat(order.getStatus()).isEqualTo(OrderStatusEnum.EM_EXECUCAO);
    }

    @Test
    void shouldThrowException_whenTransitioningToInProgress_andHasStockPending() {
        ServiceOrder order = ServiceOrderMock.approvedWithStockPending();

        assertThatThrownBy(order::startExecution)
                .isInstanceOf(InvalidOrderTransitionException.class)
                .hasMessageContaining("pendências de estoque");
    }

    @Test
    void shouldTransitionToFinished_whenAllTasksAreFinished() {
        ServiceOrder order = ServiceOrderMock.withAllTasksFinished();

        order.finish();

        assertThat(order.getStatus()).isEqualTo(OrderStatusEnum.FINALIZADO);
        assertThat(order.getCompletedAt()).isNotNull();
    }

    @Test
    void shouldThrowException_whenTransitioningToFinished_andHasUnfinishedTasks() {
        ServiceOrder order = ServiceOrderMock.withOneUnfinishedTask();

        assertThatThrownBy(order::finish)
                .isInstanceOf(InvalidOrderTransitionException.class)
                .hasMessageContaining("não finalizados");
    }

    @Test
    void shouldTransitionToDelivered_whenOrderIsFinished() {
        ServiceOrder order = ServiceOrderMock.finished();

        order.deliver();

        assertThat(order.getStatus()).isEqualTo(OrderStatusEnum.ENTREGUE);
        assertThat(order.getDeliveredAt()).isNotNull();
    }
}

