package com.fiap.mechanical_hub.application.usecases;

import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderDetailResponse;
import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderResponse;
import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderSummaryResponse;
import com.fiap.mechanical_hub.application.mappers.ServiceOrderMapper;
import com.fiap.mechanical_hub.domain.entities.Customer;
import com.fiap.mechanical_hub.domain.entities.OrderTask;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.entities.Vehicle;
import com.fiap.mechanical_hub.domain.enums.OrderStatus;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.infrastructure.database.repositories.adapter.CustomerRepositoryAdapter;
import com.fiap.mechanical_hub.infrastructure.database.repositories.adapter.OrderTaskRepositoryAdapter;
import com.fiap.mechanical_hub.infrastructure.database.repositories.adapter.ServiceOrderRepositoryAdapter;
import com.fiap.mechanical_hub.infrastructure.database.repositories.adapter.VehicleRepositoryAdapter;
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

    private final CustomerRepositoryAdapter customerRepository;
    private final VehicleRepositoryAdapter vehicleRepository;

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

        var orderTasks = findOrderTasksByServiceOrderId(orderId);
        updatedOrder.setOrderTasks(orderTasks);

        return mapper.toResponse(updatedOrder);
    }

    @Transactional(readOnly = true)
    public ServiceOrderDetailResponse findById(UUID id) {

        ServiceOrder order = serviceOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Service order with id " + id + " not found"));

        var customer = customerRepository.findById(order.getCustomerId()).orElse(null);
        var vehicle = vehicleRepository.findById(order.getVehicleId()).orElse(null);
        var tasks = findOrderTasksByServiceOrderId(id);

        order.setOrderTasks(tasks);

        return mapper.toDetailResponse(order, customer, vehicle);
    }

    private List<OrderTask> findOrderTasksByServiceOrderId(UUID serviceOrderId) {
        try {
            return orderTaskRepository.findByServiceOrderId(serviceOrderId);
        }catch (Exception e){
            throw new RuntimeException("Error retrieving order tasks for service order with id " + serviceOrderId + ": " + e.getMessage(), e);
        }
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

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        List<ServiceOrder> orders;
        try {
            orders = serviceOrderRepository.findSummaryByCustomerId(customerId);
        }catch (Exception e){
            throw new RuntimeException("Error retrieving service orders for customer with id " + customerId + ": " + e.getMessage(), e);
        }

        return orders.stream()
                .map(order -> {
                    Vehicle vehicle = vehicleRepository.findById(order.getVehicleId()).orElse(null);
                    return mapper.toSummaryResponse(order, customer, vehicle);
                })
                .toList();
    }

    public List<ServiceOrderSummaryResponse> findAll(String status, UUID customerId, LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        try {
            return serviceOrderRepository.findAllSummaries(status, customerId, startDate, endDate);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving service orders: " + e.getMessage(), e);
        }
    }
}
