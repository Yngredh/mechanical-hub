package com.fiap.mechanical_hub.application.mappers;

import com.fiap.mechanical_hub.application.dto.serviceorder.OrderTaskResponse;
import com.fiap.mechanical_hub.domain.entities.OrderTask;
import com.fiap.mechanical_hub.mocks.domain.entities.OrderTaskMock;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTaskMapperTest {

    @Test
    void shouldMapAllFields_whenConvertingToTaskResponse() {
        OrderTask task = OrderTaskMock.notStarted();

        OrderTaskResponse response = OrderTaskMapper.toTaskResponse(task);

        assertThat(response.getId()).isEqualTo(task.getId());
        assertThat(response.getServiceOrderId()).isEqualTo(task.getServiceOrderId());
        assertThat(response.getStartedAt()).isEqualTo(task.getStartedAt());
        assertThat(response.getFinishedAt()).isEqualTo(task.getFinishedAt());
    }

    @Test
    void shouldMapStatusDisplayName_whenConvertingToTaskResponse() {
        OrderTask task = OrderTaskMock.notStarted();

        OrderTaskResponse response = OrderTaskMapper.toTaskResponse(task);

        assertThat(response.getStatus()).isEqualTo(task.getStatus().getDisplayName());
    }

    @Test
    void shouldMapServiceData_whenConvertingToTaskResponse() {
        OrderTask task = OrderTaskMock.finished();

        OrderTaskResponse response = OrderTaskMapper.toTaskResponse(task);

        assertThat(response.getService()).isNotNull();
        assertThat(response.getService().getId()).isEqualTo(task.getServiceData().getId());
        assertThat(response.getService().getName()).isEqualTo(task.getServiceData().getName());
    }
}
