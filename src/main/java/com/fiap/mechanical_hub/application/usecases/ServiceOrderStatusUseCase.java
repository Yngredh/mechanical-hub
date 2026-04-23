package com.fiap.mechanical_hub.application.usecases;

import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderResponse;
import com.fiap.mechanical_hub.application.mappers.ServiceOrderMapper;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.enums.OrderStatus;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.OrderTaskRepository;
import com.fiap.mechanical_hub.domain.repositories.ServiceOrderRepository;
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

    public void updateStatus(UUID orderId, String newStatusString, String userProfile) {
        ServiceOrder order = serviceOrderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Service order with id " + orderId + " not found"));

        OrderStatus newStatus = OrderStatus.fromString(newStatusString);

        switch (newStatus) {
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
            case CANCELADO:
                order.cancel();
                break;
            default:
                throw new IllegalArgumentException("Status transition not supported: " + newStatusString);
        }

        ServiceOrder updatedOrder = serviceOrderRepository.save(order);

        var orderTasks = orderTaskRepository.findByServiceOrderId(orderId);
        updatedOrder.setOrderTasks(orderTasks);

        mapper.toResponse(updatedOrder);
    }

    @Transactional(readOnly = true)
    public ServiceOrderResponse findById(UUID id) {
        ServiceOrder order = serviceOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Service order with id " + id + " not found"));

        var tasks = orderTaskRepository.findByServiceOrderId(id);
        order.setOrderTasks(tasks);

        return mapper.toResponse(order);
    }
}

