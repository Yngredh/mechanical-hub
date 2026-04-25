package com.fiap.mechanical_hub.application.usecases;

import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderResponse;
import com.fiap.mechanical_hub.application.mappers.ServiceOrderMapper;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.enums.OrderStatus;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.OrderTaskRepository;
import com.fiap.mechanical_hub.domain.repositories.ServiceOrderRepository;
import com.fiap.mechanical_hub.infrastructure.integrations.whatsapp.WhatsAppMessenger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional
public class ServiceOrderStatusUseCase {

    private final ServiceOrderRepository serviceOrderRepository;
    private final OrderTaskRepository orderTaskRepository;
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
    public ServiceOrderResponse findById(UUID id) {
        ServiceOrder order = serviceOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Service order with id " + id + " not found"));

        var tasks = orderTaskRepository.findByServiceOrderId(id);
        order.setOrderTasks(tasks);

        return mapper.toResponse(order);
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


}
