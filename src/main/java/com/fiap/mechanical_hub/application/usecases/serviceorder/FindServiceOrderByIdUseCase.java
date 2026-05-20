package com.fiap.mechanical_hub.application.usecases.serviceorder;

import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderDetailResponse;
import com.fiap.mechanical_hub.application.dto.customer.CustomerResponse;
import com.fiap.mechanical_hub.application.dto.vehicle.VehicleResponse;
import com.fiap.mechanical_hub.application.usecases.customer.FindCustomerByIdUseCase;
import com.fiap.mechanical_hub.application.usecases.vehicle.FindVehicleByIdUseCase;
import com.fiap.mechanical_hub.domain.entities.OrderTask;
import com.fiap.mechanical_hub.domain.entities.ServiceData;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.ServiceOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FindServiceOrderByIdUseCase {

    private final ServiceOrderRepository repository;
    private final FindVehicleByIdUseCase findVehicleByIdUseCase;
    private final FindCustomerByIdUseCase findCustomerByIdUseCase;

    @Transactional(readOnly = true)
    public ServiceOrderDetailResponse execute(UUID id) {
        log.info("Retrieving service order with id: {}", id);

        ServiceOrder order = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ordem de serviço não encontrada com ID: " + id));

        VehicleResponse vehicle = findVehicleByIdUseCase.execute(order.getVehicleId());
        CustomerResponse customer = findCustomerByIdUseCase.execute(order.getCustomerId());
        List<ServiceData> serviceData = order.getOrderTasks()
                .stream()
                .map(OrderTask::getServiceData)
                .toList();

        return new ServiceOrderDetailResponse(
                order.getId(),
                order.getOrderNumber(),
                customer,
                vehicle,
                order.getStatus().name(),
                order.getRequestDescription(),
                order.getBudget(),
                order.isHasStockPending(),
                serviceData,
                order.getOrderTasks(),
                order.getCreatedAt()
        );
    }
}


