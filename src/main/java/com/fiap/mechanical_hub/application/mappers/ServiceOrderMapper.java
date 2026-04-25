package com.fiap.mechanical_hub.application.mappers;

import com.fiap.mechanical_hub.application.dto.customer.CustomerResponse;
import com.fiap.mechanical_hub.application.dto.serviceorder.OrderTaskResponse;
import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderDetailResponse;
import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderResponse;
import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderSummaryResponse;
import com.fiap.mechanical_hub.application.dto.vehicle.VehicleResponse;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.entities.OrderTask;
import com.fiap.mechanical_hub.domain.repositories.CustomerRepository;
import com.fiap.mechanical_hub.domain.repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ServiceOrderMapper {

    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    private final CustomerMapper customerMapper;
    private final VehicleMapper vehicleMapper;

    public ServiceOrderResponse toResponse(ServiceOrder order) {
        List<OrderTaskResponse> orderTasks = order.getOrderTasks() != null 
            ? order.getOrderTasks().stream()
                .map(this::toTaskResponse)
                .collect(Collectors.toList())
            : List.of();

        return new ServiceOrderResponse(
                order.getId(),
                order.getVehicleId(),
                order.getCustomerId(),
                order.getStatus().getDisplayName(),
                order.getCreatedByUserId(),
                order.getResponsibleUserId(),
                order.getOrderNumber(),
                order.getRequestDescription(),
                order.getBudget(),
                order.isHasStockPending(),
                order.getEstimatedCompletionAt(),
                order.getOpenedAt(),
                order.getCompletedAt(),
                order.getDeliveredAt(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                orderTasks
        );
    }

    public ServiceOrderSummaryResponse toSummaryResponse(ServiceOrder order) {
        var customer = customerRepository.findById(order.getCustomerId()).orElse(null);
        var vehicle = vehicleRepository.findById(order.getVehicleId()).orElse(null);

        String customerName = customer != null ? customer.getName() : "Unknown";
        String vehicleInfo = vehicle != null ? vehicle.getBrand() + " " + vehicle.getModel() + " (" + vehicle.getLicensePlate() + ")" : "Unknown";

        return new ServiceOrderSummaryResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getStatus().getDisplayName(),
                customerName,
                vehicleInfo,
                order.getBudget(),
                order.getCreatedAt()
        );
    }

    public ServiceOrderDetailResponse toDetailResponse(ServiceOrder order) {
        var customer = customerRepository.findById(order.getCustomerId()).orElse(null);
        var vehicle = vehicleRepository.findById(order.getVehicleId()).orElse(null);

        CustomerResponse customerResponse = customer != null ? customerMapper.toResponse(customer) : null;
        VehicleResponse vehicleResponse = vehicle != null ? vehicleMapper.toResponse(vehicle) : null;

        List<OrderTaskResponse> orderTasks = order.getOrderTasks() != null 
            ? order.getOrderTasks().stream()
                .map(this::toTaskResponse)
                .collect(Collectors.toList())
            : List.of();

        return new ServiceOrderDetailResponse(
                order.getId(),
                order.getVehicleId(),
                order.getCustomerId(),
                order.getStatus().getDisplayName(),
                order.getCreatedByUserId(),
                order.getResponsibleUserId(),
                order.getOrderNumber(),
                order.getRequestDescription(),
                order.getBudget(),
                order.isHasStockPending(),
                order.getEstimatedCompletionAt(),
                order.getOpenedAt(),
                order.getCompletedAt(),
                order.getDeliveredAt(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                customerResponse,
                vehicleResponse,
                orderTasks
        );
    }

    public OrderTaskResponse toTaskResponse(OrderTask task) {
        return new OrderTaskResponse(
                task.getId(),
                task.getServiceOrderId(),
                task.getServiceId(),
                task.getStatus().getDisplayName(),
                task.getStartedAt(),
                task.getFinishedAt()
        );
    }
}
