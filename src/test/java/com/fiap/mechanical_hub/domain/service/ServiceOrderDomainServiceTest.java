package com.fiap.mechanical_hub.domain.service;

import com.fiap.mechanical_hub.mocks.domain.entities.ServiceOrderMock;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;

class ServiceOrderDomainServiceTest {

    private final ServiceOrderDomainService service = new ServiceOrderDomainService();

    @Test
    void shouldNotThrowException_whenNoOrdersAreOpen() {
        List<ServiceOrder> orders = new ArrayList<>();
        orders.add(ServiceOrderMock.finished());
        orders.add(ServiceOrderMock.delivered());

        assertThatCode(() -> service.hasAnyOpenServiceOrder(orders))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowException_whenOrderIsOpen() {
        List<ServiceOrder> orders = new ArrayList<>();
        orders.add(ServiceOrderMock.inDiagnosis());

        assertThatThrownBy(() -> service.hasAnyOpenServiceOrder(orders))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("ordens abertas");
    }

    @Test
    void shouldThrowException_whenAnyOrderInListIsOpen() {
        List<ServiceOrder> orders = new ArrayList<>();
        orders.add(ServiceOrderMock.finished());
        orders.add(ServiceOrderMock.inProgress());
        orders.add(ServiceOrderMock.delivered());

        assertThatThrownBy(() -> service.hasAnyOpenServiceOrder(orders))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("ordens abertas");
    }

    @Test
    void shouldValidateReceivedOrder() {
        List<ServiceOrder> orders = new ArrayList<>();
        orders.add(ServiceOrderMock.received());

        assertThatThrownBy(() -> service.hasAnyOpenServiceOrder(orders))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void shouldValidateInDiagnosisOrder() {
        List<ServiceOrder> orders = new ArrayList<>();
        orders.add(ServiceOrderMock.inDiagnosis());

        assertThatThrownBy(() -> service.hasAnyOpenServiceOrder(orders))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void shouldValidateWaitingApprovalOrder() {
        List<ServiceOrder> orders = new ArrayList<>();
        orders.add(ServiceOrderMock.waitingApproval());

        assertThatThrownBy(() -> service.hasAnyOpenServiceOrder(orders))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void shouldValidateApprovedOrder() {
        List<ServiceOrder> orders = new ArrayList<>();
        orders.add(ServiceOrderMock.approvedWithoutStockPending());

        assertThatThrownBy(() -> service.hasAnyOpenServiceOrder(orders))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void shouldValidateInProgressOrder() {
        List<ServiceOrder> orders = new ArrayList<>();
        orders.add(ServiceOrderMock.inProgress());

        assertThatThrownBy(() -> service.hasAnyOpenServiceOrder(orders))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void shouldNotThrowException_whenRejectedOrder() {
        List<ServiceOrder> orders = new ArrayList<>();
        orders.add(ServiceOrderMock.rejected());

        assertThatCode(() -> service.hasAnyOpenServiceOrder(orders))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldNotThrowException_whenFinishedOrder() {
        List<ServiceOrder> orders = new ArrayList<>();
        orders.add(ServiceOrderMock.finished());

        assertThatCode(() -> service.hasAnyOpenServiceOrder(orders))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldNotThrowException_whenEmptyList() {
        List<ServiceOrder> orders = new ArrayList<>();

        assertThatCode(() -> service.hasAnyOpenServiceOrder(orders))
                .doesNotThrowAnyException();
    }
}