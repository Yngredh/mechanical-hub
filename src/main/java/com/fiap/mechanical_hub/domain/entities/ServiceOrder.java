package com.fiap.mechanical_hub.domain.entities;

import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;
import com.fiap.mechanical_hub.domain.exceptions.BusinessRuleException;
import com.fiap.mechanical_hub.domain.exceptions.InvalidOrderTransitionException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
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
    private List<OrderTask> orderTasks = new ArrayList<>();

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

    public void startDiagnosis() {
        validateCurrentStatus(OrderStatusEnum.RECEBIDO);

        this.status = OrderStatusEnum.EM_DIAGNOSTICO;
        this.openedAt = LocalDateTime.now();
    }

    public void submitForApproval() {
        validateCurrentStatus(OrderStatusEnum.EM_DIAGNOSTICO);

        if (budget == null || budget.equals(BigDecimal.ZERO)) {
            throw new BusinessRuleException("Order budget not generated");
        }

        this.status = OrderStatusEnum.AGUARDANDO_APROVACAO;
    }

    public void approve() {
        validateCurrentStatus(OrderStatusEnum.AGUARDANDO_APROVACAO);

        this.status = OrderStatusEnum.APROVADO;
    }

    public void reject() {
        validateCurrentStatus(OrderStatusEnum.AGUARDANDO_APROVACAO);

        this.status = OrderStatusEnum.RECUSADO;
    }

    public void startExecution() {
        validateCurrentStatus(OrderStatusEnum.APROVADO);

        if (hasStockPending) {
            throw new InvalidOrderTransitionException("Não é possível executar uma ordem com pendências de estoque"); }
        this.status = OrderStatusEnum.EM_EXECUCAO;
    }

    public void finish() {
        validateCurrentStatus(OrderStatusEnum.EM_EXECUCAO);

        boolean allFinished = orderTasks.stream().allMatch(OrderTask::isFinished);

        if (!allFinished) { throw new InvalidOrderTransitionException("Não é possível finalizar ordem, há serviços não finalizados."); }

        this.status = OrderStatusEnum.FINALIZADO;
        this.completedAt = LocalDateTime.now();
    }

    public void deliver() {
        validateCurrentStatus(OrderStatusEnum.FINALIZADO);

        this.status = OrderStatusEnum.ENTREGUE;
        this.deliveredAt = LocalDateTime.now();
    }

    private void validateCurrentStatus(OrderStatusEnum expected) {
        if (status != expected) { throw new InvalidOrderTransitionException("Invalid transition from " + status); }
    }

    public void addTask(OrderTask task) {
        if (this.orderTasks == null) { this.orderTasks = new ArrayList<>(); }
        this.orderTasks.add(task);
    }

    public boolean validateTaskNotDuplicated(UUID serviceId) {
        return this.orderTasks.stream()
                .anyMatch(ot -> ot.getServiceData().getId().equals(serviceId));
    }

    private OrderTask findTask(UUID taskId) {
        return this.getOrderTasks().stream()
                .filter(t -> t.getServiceData().getId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new BusinessRuleException("Tarefa não encontrada"));
    }

    public void startTask(UUID taskId){
        OrderTask task = findTask(taskId);
        task.start();
        if (this.status == OrderStatusEnum.APROVADO) { this.startExecution(); }
    }

    public void finishTask(UUID taskId) {
        OrderTask task = findTask(taskId);
        task.finish();
    }

    public void updateBudget(BigDecimal newBudget) {
        this.budget = newBudget;
        this.updatedAt = LocalDateTime.now();
    }

    public void setHasStockPending(Boolean hasPending) {
        this.hasStockPending = hasPending;
        this.updatedAt = LocalDateTime.now();
    }

    public void isAddingServiceAvailable() {
        if (!this.getStatus().equals(OrderStatusEnum.EM_DIAGNOSTICO)) {
            throw new BusinessRuleException("Serviços só podem ser adicionados enquanto a OS está em 'Em diagnóstico'.");
        }
    }

    public boolean isOrderOpen() {
        return this.getStatus() != OrderStatusEnum.RECUSADO
                && this.getStatus() != OrderStatusEnum.FINALIZADO
                && this.getStatus() != OrderStatusEnum.ENTREGUE;
    }

}