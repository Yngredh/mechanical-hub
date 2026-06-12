package com.fiap.mechanical_hub.infrastructure.database.mappers;

import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.infrastructure.database.models.ServiceOrderModel;
import com.fiap.mechanical_hub.mocks.domain.entities.ServiceOrderMock;
import com.fiap.mechanical_hub.mocks.infrastructure.database.models.ServiceOrderModelMock;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceOrderRepositoryMapperTest {

    @Test
    void shouldMapAllFields_whenConvertingDomainToJpaEntity() {
        ServiceOrder order = ServiceOrderMock.received();

        ServiceOrderModel model = ServiceOrderRepositoryMapper.toJpaEntity(order);

        assertThat(model.getId()).isEqualTo(order.getId());
        assertThat(model.getVehicleId()).isEqualTo(order.getVehicleId());
        assertThat(model.getCustomerId()).isEqualTo(order.getCustomerId());
        assertThat(model.getOrderStatusEnum()).isEqualTo(order.getStatus());
        assertThat(model.getCreatedByUserId()).isEqualTo(order.getCreatedByUserId());
        assertThat(model.getOrderNumber()).isEqualTo(order.getOrderNumber());
        assertThat(model.getRequestDescription()).isEqualTo(order.getRequestDescription());
        assertThat(model.getBudget()).isEqualTo(order.getBudget());
        assertThat(model.isHasStockPending()).isEqualTo(order.isHasStockPending());
    }

    @Test
    void shouldMapAllFields_whenConvertingJpaEntityToDomainEntity() {
        ServiceOrderModel model = ServiceOrderModelMock.received();

        ServiceOrder order = ServiceOrderRepositoryMapper.toDomainEntity(model);

        assertThat(order.getId()).isEqualTo(model.getId());
        assertThat(order.getVehicleId()).isEqualTo(model.getVehicleId());
        assertThat(order.getCustomerId()).isEqualTo(model.getCustomerId());
        assertThat(order.getStatus()).isEqualTo(model.getOrderStatusEnum());
        assertThat(order.getCreatedByUserId()).isEqualTo(model.getCreatedByUserId());
        assertThat(order.getOrderNumber()).isEqualTo(model.getOrderNumber());
        assertThat(order.getRequestDescription()).isEqualTo(model.getRequestDescription());
        assertThat(order.getBudget()).isEqualTo(model.getBudget());
        assertThat(order.isHasStockPending()).isEqualTo(model.isHasStockPending());
    }

    @Test
    void shouldMapOrderTasks_whenOrderHasTasks() {
        ServiceOrderModel model = ServiceOrderModelMock.withOneTask();

        ServiceOrder order = ServiceOrderRepositoryMapper.toDomainEntity(model);

        assertThat(order.getOrderTasks()).hasSize(1);
        assertThat(order.getOrderTasks().getFirst().getId())
                .isEqualTo(model.getOrderTasks().getFirst().getId());
    }

    @Test
    void shouldMapOrderTasksToModel_whenConvertingDomainWithTasksToJpaEntity() {
        ServiceOrder order = ServiceOrderMock.withOneUnfinishedTask();

        ServiceOrderModel model = ServiceOrderRepositoryMapper.toJpaEntity(order);

        assertThat(model.getOrderTasks()).hasSize(1);
    }
}
