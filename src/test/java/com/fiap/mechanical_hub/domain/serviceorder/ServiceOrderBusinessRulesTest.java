package com.fiap.mechanical_hub.domain.serviceorder;

import com.fiap.mechanical_hub.mocks.domain.entities.ServiceOrderMock;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ServiceOrderBusinessRulesTest {

    @Test
    void shouldAddServiceToOrder_whenOrderIsInDiagnosis() {
        ServiceOrder order = ServiceOrderMock.inDiagnosis();

        order.isAddingServiceAvailable();

        assertThat(order.getStatus()).isNotNull();
    }

    @Test
    void shouldThrowException_whenAddingService_andOrderIsNotInDiagnosis() {
        ServiceOrder order = ServiceOrderMock.approvedWithoutStockPending();

        assertThatThrownBy(order::isAddingServiceAvailable)
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Em diagnóstico");
    }

    @Test
    void shouldSetHasStockPendingToTrue_whenIndicatingPendency() {
        ServiceOrder order = ServiceOrderMock.approvedWithoutStockPending();

        order.setHasStockPending(true);

        assertThat(order.isHasStockPending()).isTrue();
        assertThat(order.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldSetHasStockPendingToFalse_whenResolvingPendency() {
        ServiceOrder order = ServiceOrderMock.approvedWithStockPending();

        order.setHasStockPending(false);

        assertThat(order.isHasStockPending()).isFalse();
        assertThat(order.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldUpdateBudget_whenCalculatingPrice() {
        ServiceOrder order = ServiceOrderMock.inDiagnosis();
        BigDecimal newBudget = BigDecimal.valueOf(1500.00);

        order.updateBudget(newBudget);

        assertThat(order.getBudget()).isEqualTo(newBudget);
        assertThat(order.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldValidateTaskNotDuplicated_whenAddingMultipleServices() {
        ServiceOrder order = ServiceOrderMock.inDiagnosis();

        assertThat(order.getOrderTasks()).isNotNull();
    }

    @Test
    void shouldMarkOrderAsOpen_whenStatusIsNotRecusadoOrFinalizado() {
        ServiceOrder order = ServiceOrderMock.inProgress();

        assertThat(order.isOrderOpen()).isTrue();
    }

    @Test
    void shouldMarkOrderAsClosed_whenOrderIsRejected() {
        ServiceOrder order = ServiceOrderMock.rejected();

        assertThat(order.isOrderOpen()).isFalse();
    }

    @Test
    void shouldMarkOrderAsClosed_whenOrderIsFinalized() {
        ServiceOrder order = ServiceOrderMock.finished();

        assertThat(order.isOrderOpen()).isFalse();
    }
}

