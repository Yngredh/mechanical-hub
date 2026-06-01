package com.fiap.mechanical_hub.domain.entities;

import com.fiap.mechanical_hub.mocks.domain.entities.OrderTaskMock;
import com.fiap.mechanical_hub.domain.enums.TaskStatusEnum;
import com.fiap.mechanical_hub.domain.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTaskTest {

    @Test
    void shouldStartTask_whenTaskIsNotStarted() {
        OrderTask task = OrderTaskMock.notStarted();

        task.start();

        assertThat(task.getStatus()).isEqualTo(TaskStatusEnum.INICIADO);
        assertThat(task.getStartedAt()).isNotNull();
    }

    @Test
    void shouldFinishTask_whenTaskIsStarted() {
        OrderTask task = OrderTaskMock.started();

        task.finish();

        assertThat(task.getStatus()).isEqualTo(TaskStatusEnum.FINALIZADO);
        assertThat(task.getFinishedAt()).isNotNull();
    }

    @Test
    void shouldThrowException_whenStartingAlreadyFinishedTask() {
        OrderTask task = OrderTaskMock.finished();

        assertThatThrownBy(task::start)
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Transição de status não permitida");
    }

    @Test
    void shouldThrowException_whenFinishingTaskThatWasNotStarted() {
        OrderTask task = OrderTaskMock.notStarted();

        assertThatThrownBy(task::finish)
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Transição de status não permitida");
    }

    @Test
    void shouldRecordStartedAt_whenTaskStarts() {
        OrderTask task = OrderTaskMock.notStarted();

        task.start();

        assertThat(task.getStartedAt()).isNotNull();
    }

    @Test
    void shouldRecordFinishedAt_whenTaskFinishes() {
        OrderTask task = OrderTaskMock.started();

        task.finish();

        assertThat(task.getFinishedAt()).isNotNull();
    }

    @Test
    void shouldReturnTrue_whenTaskIsFinished() {
        OrderTask task = OrderTaskMock.finished();

        assertThat(task.isFinished()).isTrue();
    }

    @Test
    void shouldReturnFalse_whenTaskIsNotFinished() {
        OrderTask task = OrderTaskMock.notStarted();

        assertThat(task.isFinished()).isFalse();
    }

    @Test
    void shouldReturnFalse_whenTaskIsStartedButNotFinished() {
        OrderTask task = OrderTaskMock.started();

        assertThat(task.isFinished()).isFalse();
    }
}

