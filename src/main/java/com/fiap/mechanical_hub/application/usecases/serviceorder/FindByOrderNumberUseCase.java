package com.fiap.mechanical_hub.application.usecases.serviceorder;

import com.fiap.mechanical_hub.application.command.serviceorder.FindByOrderNumberCommand;
import com.fiap.mechanical_hub.application.dto.serviceorder.request.ServiceOrderCustomerView;
import com.fiap.mechanical_hub.application.dto.customer.CustomerResponse;
import com.fiap.mechanical_hub.application.dto.vehicle.VehicleResponse;
import com.fiap.mechanical_hub.application.usecases.customer.FindCustomerByIdUseCase;
import com.fiap.mechanical_hub.application.usecases.vehicle.FindVehicleByIdUseCase;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.ServiceOrderRepository;
import com.fiap.mechanical_hub.infrastructure.http.mappers.ServiceOrderHttpMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FindByOrderNumberUseCase {

    private final ServiceOrderRepository repository;
    private final FindVehicleByIdUseCase findVehicleByIdUseCase;
    private final FindCustomerByIdUseCase findCustomerByIdUseCase;

    @Transactional(readOnly = true)
    public ServiceOrderCustomerView execute(FindByOrderNumberCommand command) {
        log.info("Retrieving service order by number: {}", command.orderNumber());

        ServiceOrder order = repository.findByOrderNumber(command.orderNumber())
                .orElseThrow(() -> new NotFoundException("Ordem de serviço não encontrada com número: " + command.orderNumber()));

        VehicleResponse vehicle = findVehicleByIdUseCase.execute(order.getVehicleId());
        CustomerResponse customer = findCustomerByIdUseCase.execute(order.getCustomerId());
        List<String> services = order.getOrderTasks()
                .stream()
                .map(task -> task.getServiceData().getName())
                .toList();

        return ServiceOrderHttpMapper.toCustomerView(order, vehicle, customer, services);
    }
}

