package com.fiap.mechanical_hub.infrastructure.database.mappers;

import com.fiap.mechanical_hub.domain.entities.OrderTask;
import com.fiap.mechanical_hub.domain.enums.TaskStatusEnum;
import com.fiap.mechanical_hub.infrastructure.database.models.OrderTaskModel;
import com.fiap.mechanical_hub.infrastructure.database.models.ServiceModel;
import com.fiap.mechanical_hub.infrastructure.database.models.ServiceOrderModel;
import com.fiap.mechanical_hub.mocks.domain.entities.OrderTaskMock;
import com.fiap.mechanical_hub.mocks.domain.entities.ServiceDataMock;
import com.fiap.mechanical_hub.mocks.infrastructure.database.models.OrderTaskModelMock;
import com.fiap.mechanical_hub.mocks.infrastructure.database.models.ServiceOrderModelMock;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTaskRepositoryMapperTest {

    @Test
    void shouldMapAllFields_whenConvertingJpaEntityToDomainEntity() {
        OrderTaskModel model = OrderTaskModelMock.notStarted();

        OrderTask task = OrderTaskRepositoryMapper.toDomainEntity(model);

        assertThat(task.getId()).isEqualTo(model.getId());
        assertThat(task.getServiceOrderId()).isEqualTo(model.getServiceOrder().getId());
        assertThat(task.getStatus()).isEqualTo(TaskStatusEnum.valueOf(model.getServiceStatus()));
        assertThat(task.getStartedAt()).isEqualTo(model.getStartedAt());
        assertThat(task.getFinishedAt()).isEqualTo(model.getFinishedAt());
    }

    @Test
    void shouldMapServiceData_whenConvertingJpaEntityToDomainEntity() {
        OrderTaskModel model = OrderTaskModelMock.notStarted();

        OrderTask task = OrderTaskRepositoryMapper.toDomainEntity(model);

        assertThat(task.getServiceData()).isNotNull();
        assertThat(task.getServiceData().getId()).isEqualTo(model.getService().getId());
        assertThat(task.getServiceData().getName()).isEqualTo(model.getService().getName());
    }

    @Test
    void shouldMapTimestamps_whenConvertingFinishedTaskJpaEntityToDomainEntity() {
        OrderTaskModel model = OrderTaskModelMock.finished();

        OrderTask task = OrderTaskRepositoryMapper.toDomainEntity(model);

        assertThat(task.getStartedAt()).isEqualTo(model.getStartedAt());
        assertThat(task.getFinishedAt()).isEqualTo(model.getFinishedAt());
    }

    @Test
    void shouldMapAllFields_whenConvertingDomainToJpaEntity() {
        OrderTask task = OrderTaskMock.notStarted();
        ServiceOrderModel parent = ServiceOrderModelMock.received();

        OrderTaskModel model = OrderTaskRepositoryMapper.toJpaEntity(task, parent);

        assertThat(model.getId()).isEqualTo(task.getId());
        assertThat(model.getServiceOrder()).isEqualTo(parent);
        assertThat(model.getServiceStatus()).isEqualTo(task.getStatus().name());
        assertThat(model.getStartedAt()).isEqualTo(task.getStartedAt());
        assertThat(model.getFinishedAt()).isEqualTo(task.getFinishedAt());
    }

    @Test
    void shouldMapServiceDataToServiceModel_whenConvertingToModel() {
        ServiceModel model = OrderTaskRepositoryMapper.toModel(ServiceDataMock.withDefaultValues());

        assertThat(model.getId()).isEqualTo(ServiceDataMock.withDefaultValues().getId());
        assertThat(model.getName()).isEqualTo(ServiceDataMock.withDefaultValues().getName());
        assertThat(model.getLaborCost()).isEqualTo(ServiceDataMock.withDefaultValues().getLaborCost());
        assertThat(model.getTotalPrice()).isEqualTo(ServiceDataMock.withDefaultValues().getTotalPrice());
    }
}
