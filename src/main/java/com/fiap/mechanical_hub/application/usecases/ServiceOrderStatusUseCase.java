package com.fiap.mechanical_hub.application.usecases;

import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderDetailResponse;
import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderResponse;
import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderSummaryResponse;
import com.fiap.mechanical_hub.application.mappers.ServiceOrderMapper;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.enums.OrderStatus;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.infrastructure.database.repositories.adapter.OrderTaskRepositoryAdapter;
import com.fiap.mechanical_hub.infrastructure.database.repositories.adapter.ServiceOrderRepositoryAdapter;
import com.fiap.mechanical_hub.infrastructure.integrations.whatsapp.WhatsAppMessenger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional
public class ServiceOrderStatusUseCase {

    private final ServiceOrderRepositoryAdapter serviceOrderRepository;
    private final OrderTaskRepositoryAdapter orderTaskRepository;
    private final ServiceOrderMapper mapper;
    private final WhatsAppMessenger whatsAppMessenger;

    public ServiceOrderResponse updateStatus(UUID orderId, String newStatusString, String userProfile) {
        ServiceOrder order = serviceOrderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Service order with id " + orderId + " not found"));

        OrderStatus newStatus = OrderStatus.fromString(newStatusString);

        switch (newStatus) {
            case RECEBIDO:
                order.receive(userProfile);
                break;
            case APROVADO:
                order.approve();
                break;
            case EM_DIAGNOSTICO:
                order.startDiagnosis(userProfile);
                break;
            case EM_EXECUCAO:
                order.startExecution();
                break;
            case FINALIZADO:
                var tasks = orderTaskRepository.findByServiceOrderId(orderId);
                order.finalize(tasks);
                break;
            case ENTREGUE:
                order.deliver();
                break;
            default:
                throw new IllegalArgumentException("Status transition not supported: " + newStatusString);
        }

        ServiceOrder updatedOrder = serviceOrderRepository.save(order);

        var orderTasks = orderTaskRepository.findByServiceOrderId(orderId);
        updatedOrder.setOrderTasks(orderTasks);

        return mapper.toResponse(updatedOrder);
    }

    @Transactional(readOnly = true)
    public ServiceOrderDetailResponse findById(UUID id) {
        ServiceOrder order = serviceOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Service order with id " + id + " not found"));

        var tasks = orderTaskRepository.findByServiceOrderId(id);
        order.setOrderTasks(tasks);

        return mapper.toDetailResponse(order);
    }

    @Transactional(readOnly = true)
    public List<ServiceOrderSummaryResponse> findAll(String status, UUID customerId, LocalDateTime startDate, LocalDateTime endDate) {
        List<ServiceOrder> orders = serviceOrderRepository.findAllFiltered(status, customerId, startDate, endDate);
        return orders.stream()
                .map(mapper::toSummaryResponse)
                .toList();
    }

    public ServiceOrderResponse approve(UUID serviceOrderId) {
        ServiceOrder order = serviceOrderRepository.findById(serviceOrderId)
                .orElseThrow(() -> new NotFoundException("Service order with id " + serviceOrderId + " not found"));

        whatsAppMessenger.budgetApprovalReceived(order.getOrderNumber()); //TODO: Implementar lógica de aprovação via WhatsApp
        order.approve();

        ServiceOrder updatedOrder = serviceOrderRepository.save(order);

        var orderTasks = orderTaskRepository.findByServiceOrderId(serviceOrderId);
        updatedOrder.setOrderTasks(orderTasks);

        return mapper.toResponse(updatedOrder);
    }

    @Transactional(readOnly = true)
    public List<ServiceOrderSummaryResponse> findByCustomerId(UUID customerId) {
        List<ServiceOrder> orders = serviceOrderRepository.findAllFiltered(null, customerId, null, null);
        return orders.stream()
                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt())) // descending order
                .map(mapper::toSummaryResponse)
                .toList();
    }
}
