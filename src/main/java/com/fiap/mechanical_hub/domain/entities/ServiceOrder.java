package com.fiap.mechanical_hub.domain.entities;

import com.fiap.mechanical_hub.domain.enums.OrderStatus;
import com.fiap.mechanical_hub.domain.exceptions.InvalidOrderStatusTransitionException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServiceOrder {

    private UUID id;
    private UUID vehicleId;
    private UUID customerId;
    private OrderStatus status;
    private UUID createdByUserId;
    private UUID responsibleUserId;
    private String orderNumber;
    private String requestDescription;
    private BigDecimal budget;
    private boolean hasStockPending;
    private LocalDateTime estimatedCompletionAt;
    private LocalDateTime openedAt;
    private LocalDateTime completedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Setter
    private List<OrderTask> orderTasks;

    public static ServiceOrder create(
            UUID vehicleId,
            UUID customerId,
            UUID createdByUserId,
            String orderNumber,
            String requestDescription,
            BigDecimal budget,
            LocalDateTime estimatedCompletionAt
    ) {
        ServiceOrder order = new ServiceOrder();
        order.id = UUID.randomUUID();
        order.vehicleId = vehicleId;
        order.customerId = customerId;
        order.status = OrderStatus.CRIADO;
        order.createdByUserId = createdByUserId;
        order.orderNumber = orderNumber;
        order.requestDescription = requestDescription;
        order.budget = budget;
        order.hasStockPending = false;
        order.estimatedCompletionAt = estimatedCompletionAt;
        order.createdAt = LocalDateTime.now();
        order.updatedAt = LocalDateTime.now();
        order.orderTasks = List.of();

        return order;
    }

    public void receive(String userProfile) {
        if (!isValidProfileForDiagnosis(userProfile)) {
            throw new IllegalArgumentException("Apenas Mecânico ou superior pode iniciar a ordem");
        }
    }

    public void startDiagnosis(String userProfile) {
        if (!isValidProfileForDiagnosis(userProfile)) {
            throw new IllegalArgumentException("Apenas Mecânico ou superior pode iniciar o diagnóstico");
        }

        if (status != OrderStatus.CRIADO) {
            throw new InvalidOrderStatusTransitionException(status.getDisplayName(), OrderStatus.EM_DIAGNOSTICO.getDisplayName());
        }

        this.status = OrderStatus.EM_DIAGNOSTICO;
        this.openedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void startExecution() {
        if (status != OrderStatus.EM_DIAGNOSTICO && status != OrderStatus.APROVADO) {
            throw new InvalidOrderStatusTransitionException(status.getDisplayName(), OrderStatus.EM_EXECUCAO.getDisplayName());
        }

        if (hasStockPending) {
            throw new IllegalStateException("Não é possível executar a ordem com pendência de estoque");
        }

        this.status = OrderStatus.EM_EXECUCAO;
        this.updatedAt = LocalDateTime.now();
    }

    public void finalize(List<OrderTask> tasks) {
        if (status != OrderStatus.EM_EXECUCAO) {
            throw new InvalidOrderStatusTransitionException(status.getDisplayName(), OrderStatus.FINALIZADO.getDisplayName());
        }

        boolean allTasksFinished = tasks != null && !tasks.isEmpty() &&
                tasks.stream().allMatch(OrderTask::isFinished);

        if (!allTasksFinished) {
            throw new IllegalStateException("Todos os serviços devem estar finalizados para concluir a ordem");
        }

        this.status = OrderStatus.FINALIZADO;
        this.completedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void deliver() {
        if (status != OrderStatus.FINALIZADO) {
            throw new InvalidOrderStatusTransitionException(status.getDisplayName(), OrderStatus.ENTREGUE.getDisplayName());
        }

        this.status = OrderStatus.ENTREGUE;
        this.deliveredAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateStockPendingStatus(boolean hasStockPending) {
        this.hasStockPending = hasStockPending;
        this.updatedAt = LocalDateTime.now();
    }

    public void approve() {
        if (status != OrderStatus.AGUARDANDO_APROVACAO) {
            throw new InvalidOrderStatusTransitionException(status.getDisplayName(), OrderStatus.APROVADO.getDisplayName());
        }

        this.status = OrderStatus.APROVADO;
        this.updatedAt = LocalDateTime.now();
    }

    private boolean isValidProfileForDiagnosis(String userProfile) {
        return userProfile != null &&
                (userProfile.equals("Mecânico") ||
                        userProfile.equals("Gerente") ||
                        userProfile.equals("Administrador"));
    }
}
