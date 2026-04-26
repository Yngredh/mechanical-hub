package com.fiap.mechanical_hub.domain.entities;

import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;
import com.fiap.mechanical_hub.domain.exceptions.BusinessRuleException;
import com.fiap.mechanical_hub.domain.exceptions.InvalidOrderStatusTransitionException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServiceOrder {

    private UUID id;
    private UUID vehicleId;
    private UUID customerId;
    private OrderStatusEnum status;
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
            String orderNumber,
            String requestDescription,
            UUID createdByUserId
    ) {
        if (requestDescription == null || requestDescription.isBlank()) {
            throw new BusinessRuleException("A descrição da solicitação é obrigatória");
        }
        if (requestDescription.length() > 255) {
            throw new BusinessRuleException("A descrição da solicitação não pode ultrapassar 255 caracteres");
        }

        ServiceOrder order = new ServiceOrder();
        order.id = UUID.randomUUID();
        order.vehicleId = vehicleId;
        order.customerId = customerId;
        order.status = OrderStatusEnum.RECEBIDO;
        order.createdByUserId = createdByUserId;
        order.responsibleUserId = null;
        order.orderNumber = orderNumber;
        order.requestDescription = requestDescription;
        order.budget = null;
        order.hasStockPending = false;
        order.estimatedCompletionAt = null;
        order.openedAt = null;
        order.completedAt = null;
        order.deliveredAt = null;
        order.createdAt = LocalDateTime.now();
        order.updatedAt = LocalDateTime.now();
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

        this.status = OrderStatusEnum.EM_DIAGNOSTICO;
        this.openedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void startExecution() {
        if (status != OrderStatusEnum.EM_DIAGNOSTICO) {
            throw new InvalidOrderStatusTransitionException(status.getDisplayName(), OrderStatusEnum.EM_EXECUCAO.getDisplayName());
        }

        if (hasStockPending) {
            throw new IllegalStateException("Não é possível executar a ordem com pendência de estoque");
        }

        this.status = OrderStatusEnum.EM_EXECUCAO;
        this.updatedAt = LocalDateTime.now();
    }

    public void finalize(List<OrderTask> tasks) {
        if (status != OrderStatusEnum.EM_EXECUCAO) {
            throw new InvalidOrderStatusTransitionException(status.getDisplayName(), OrderStatusEnum.FINALIZADO.getDisplayName());
        }

        boolean allTasksFinished = tasks != null && !tasks.isEmpty() &&
                tasks.stream().allMatch(OrderTask::isFinished);

        if (!allTasksFinished) {
            throw new IllegalStateException("Todos os serviços devem estar finalizados para concluir a ordem");
        }

        this.status = OrderStatusEnum.FINALIZADO;
        this.completedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void deliver() {
        if (status != OrderStatusEnum.FINALIZADO) {
            throw new InvalidOrderStatusTransitionException(status.getDisplayName(), OrderStatusEnum.ENTREGUE.getDisplayName());
        }

        this.status = OrderStatusEnum.ENTREGUE;
        this.deliveredAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateStockPendingStatus(boolean hasStockPending) {
        this.hasStockPending = hasStockPending;
        this.updatedAt = LocalDateTime.now();
    }

    public void approve() {
        if (status != OrderStatusEnum.AGUARDANDO_APROVACAO) {
            throw new InvalidOrderStatusTransitionException(status.getDisplayName(), OrderStatusEnum.APROVADO.getDisplayName());
        }

        this.status = OrderStatusEnum.APROVADO;
        this.updatedAt = LocalDateTime.now();
    }

    private boolean isValidProfileForDiagnosis(String userProfile) {
        return userProfile != null &&
                (userProfile.equals("Mecânico") ||
                        userProfile.equals("Gerente") ||
                        userProfile.equals("Administrador"));
    }
}