package com.fiap.mechanical_hub.domain.entities;

import com.fiap.mechanical_hub.domain.entities.constants.TestConstants;
import com.fiap.mechanical_hub.domain.entities.mocks.OrderTaskMock;
import com.fiap.mechanical_hub.domain.entities.mocks.ServiceMock;
import com.fiap.mechanical_hub.domain.entities.mocks.ServiceOrderMock;
import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;
import com.fiap.mechanical_hub.domain.exceptions.BusinessRuleException;
import com.fiap.mechanical_hub.domain.exceptions.InvalidOrderTransitionException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ServiceOrderTest {

    @Test
    void shouldCreateServiceOrderWithValidData() {
        ServiceOrder order = ServiceOrderMock.defaultServiceOrder();

        assertNotNull(order.getId());
        assertEquals(TestConstants.DEFAULT_VEHICLE_ID, order.getVehicleId());
        assertEquals(TestConstants.DEFAULT_CUSTOMER_ID, order.getCustomerId());
        assertEquals(TestConstants.DEFAULT_ORDER_NUMBER, order.getOrderNumber());
        assertEquals(TestConstants.DEFAULT_REQUEST_DESCRIPTION, order.getRequestDescription());
        assertEquals(TestConstants.DEFAULT_USER_ID, order.getCreatedByUserId());
        assertEquals(OrderStatusEnum.RECEBIDO, order.getStatus());
        assertFalse(order.isHasStockPending());
        assertNotNull(order.getCreatedAt());
    }

    @Test
    void shouldTransitionFromRecebidoToDiagnostico() {
        ServiceOrder order = ServiceOrderMock.defaultServiceOrder();
        assertEquals(OrderStatusEnum.RECEBIDO, order.getStatus());

        order.startDiagnosis();

        assertEquals(OrderStatusEnum.EM_DIAGNOSTICO, order.getStatus());
        assertNotNull(order.getOpenedAt());
    }

    @Test
    void shouldTransitionFromDiagnosticoToAguardandoAprovacao() {
        ServiceOrder order = ServiceOrderMock.serviceOrderInDiagnosis();
        order.updateBudget(TestConstants.DEFAULT_ORDER_BUDGET);

        order.submitForApproval();

        assertEquals(OrderStatusEnum.AGUARDANDO_APROVACAO, order.getStatus());
    }

    @Test
    void shouldTransitionFromAguardandoAprovacaoToAprovado() {
        ServiceOrder order = ServiceOrderMock.serviceOrderAwaitingApproval();

        order.approve();

        assertEquals(OrderStatusEnum.APROVADO, order.getStatus());
    }

    @Test
    void shouldTransitionFromAguardandoAprovacaoToRecusado() {
        ServiceOrder order = ServiceOrderMock.serviceOrderAwaitingApproval();

        order.reject();

        assertEquals(OrderStatusEnum.RECUSADO, order.getStatus());
    }

    @Test
    void shouldTransitionFromAprovadoToEmExecucao() {
        ServiceOrder order = ServiceOrderMock.serviceOrderApproved();

        order.startExecution();

        assertEquals(OrderStatusEnum.EM_EXECUCAO, order.getStatus());
    }

    @Test
    void shouldTransitionFromEmExecucaoToFinalizado() {
        ServiceOrder order = ServiceOrderMock.serviceOrderInExecution();
        OrderTask task = OrderTaskMock.orderTaskWithCustomValues(order.getId(), ServiceMock.defaultService());
        order.addTask(task);
        task.start();
        task.finish();

        order.finish();

        assertEquals(OrderStatusEnum.FINALIZADO, order.getStatus());
        assertNotNull(order.getCompletedAt());
    }

    @Test
    void shouldTransitionFromFinalizadoToEntregue() {
        ServiceOrder order = ServiceOrderMock.serviceOrderFinalized();

        order.deliver();

        assertEquals(OrderStatusEnum.ENTREGUE, order.getStatus());
        assertNotNull(order.getDeliveredAt());
    }

    @Test
    void shouldThrowExceptionWhenStartingDiagnosisFromInvalidStatus() {
        ServiceOrder order = ServiceOrderMock.serviceOrderInDiagnosis();

        assertThrows(InvalidOrderTransitionException.class, order::startDiagnosis);
    }

    @Test
    void shouldThrowExceptionWhenSubmittingForApprovalWithoutBudget() {
        ServiceOrder order = ServiceOrderMock.serviceOrderInDiagnosis();

        assertThrows(BusinessRuleException.class, order::submitForApproval);
    }

    @Test
    void shouldThrowExceptionWhenSubmittingForApprovalWithZeroBudget() {
        ServiceOrder order = ServiceOrderMock.serviceOrderInDiagnosis();
        order.updateBudget(BigDecimal.ZERO);

        assertThrows(BusinessRuleException.class, order::submitForApproval);
    }

    @Test
    void shouldThrowExceptionWhenStartingExecutionWithStockPending() {
        ServiceOrder order = ServiceOrderMock.serviceOrderWithStockPending();

        assertThrows(InvalidOrderTransitionException.class, order::startExecution);
    }

    @Test
    void shouldThrowExceptionWhenFinishingWithUnfinishedTasks() {
        ServiceOrder order = ServiceOrderMock.serviceOrderInExecution();
        OrderTask task = OrderTaskMock.orderTaskWithCustomValues(order.getId(), ServiceMock.defaultService());
        order.addTask(task);
        task.start();

        assertThrows(InvalidOrderTransitionException.class, order::finish);
    }

    @Test
    void shouldAddTaskToOrder() {
        ServiceOrder order = ServiceOrderMock.defaultServiceOrder();
        OrderTask task = OrderTaskMock.orderTaskWithCustomValues(order.getId(), ServiceMock.defaultService());

        order.addTask(task);

        assertEquals(1, order.getOrderTasks().size());
    }

    @Test
    void shouldDetectDuplicateTask() {
        ServiceOrder order = ServiceOrderMock.defaultServiceOrder();
        ServiceData service = ServiceMock.defaultService();
        OrderTask task1 = OrderTaskMock.orderTaskWithCustomService(service);

        order.addTask(task1);

        assertTrue(order.validateTaskNotDuplicated(service.getId()));
    }

    @Test
    void shouldUpdateBudget() {
        ServiceOrder order = ServiceOrderMock.defaultServiceOrder();
        BigDecimal newBudget = BigDecimal.valueOf(1000.00);

        order.updateBudget(newBudget);

        assertEquals(newBudget, order.getBudget());
    }

    @Test
    void shouldUpdateHasStockPending() {
        ServiceOrder order = ServiceOrderMock.defaultServiceOrder();
        assertFalse(order.isHasStockPending());

        order.setHasStockPending(true);

        assertTrue(order.isHasStockPending());
    }

    @Test
    void shouldOnlyAllowAddingServicesInDiagnosisStatus() {
        ServiceOrder order = ServiceOrderMock.serviceOrderAwaitingApproval();

        assertThrows(BusinessRuleException.class, order::isAddingServiceAvailable);
    }

    @Test
    void shouldThrowExceptionWhenCreatingWithBlankDescription() {
        assertThrows(BusinessRuleException.class, () ->
            ServiceOrder.create(
                    TestConstants.DEFAULT_VEHICLE_ID,
                    TestConstants.DEFAULT_CUSTOMER_ID,
                    TestConstants.DEFAULT_ORDER_NUMBER,
                    "",
                    TestConstants.DEFAULT_USER_ID
            )
        );
    }

    @Test
    void shouldThrowExceptionWhenCreatingWithNullDescription() {
        assertThrows(BusinessRuleException.class, () ->
            ServiceOrder.create(
                    TestConstants.DEFAULT_VEHICLE_ID,
                    TestConstants.DEFAULT_CUSTOMER_ID,
                    TestConstants.DEFAULT_ORDER_NUMBER,
                    null,
                    TestConstants.DEFAULT_USER_ID
            )
        );
    }

    @Test
    void shouldThrowExceptionWhenCreatingWithDescriptionTooLong() {
        String longDescription = "a".repeat(256);

        assertThrows(BusinessRuleException.class, () ->
            ServiceOrder.create(
                    TestConstants.DEFAULT_VEHICLE_ID,
                    TestConstants.DEFAULT_CUSTOMER_ID,
                    TestConstants.DEFAULT_ORDER_NUMBER,
                    longDescription,
                    TestConstants.DEFAULT_USER_ID
            )
        );
    }

}

