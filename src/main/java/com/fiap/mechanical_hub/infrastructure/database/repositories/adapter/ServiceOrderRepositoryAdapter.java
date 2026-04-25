package com.fiap.mechanical_hub.infrastructure.database.repositories.adapter;

import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.repositories.ServiceOrderRepository;
import com.fiap.mechanical_hub.infrastructure.database.models.CustomerModel;
import com.fiap.mechanical_hub.infrastructure.database.models.ServiceOrderModel;
import com.fiap.mechanical_hub.infrastructure.database.models.VehicleModel;
import com.fiap.mechanical_hub.infrastructure.database.repositories.ServiceOrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ServiceOrderRepositoryAdapter implements ServiceOrderRepository {

    private final ServiceOrderJpaRepository jpaRepository;

    @Override
    public ServiceOrder save(ServiceOrder serviceOrder) {
        ServiceOrderModel entity = toJpaEntity(serviceOrder);
        ServiceOrderModel saved = jpaRepository.save(entity);
        return toDomainEntity(saved);
    }

    @Override
    public Optional<ServiceOrder> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomainEntity);
    }

    @Override
    public List<ServiceOrder> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomainEntity)
                .toList();
    }

    @Override
    public Optional<String> findLastOrderNumberByYearMonth(String yearMonth) {
        return jpaRepository.findLastOrderNumberByYearMonth(yearMonth);
    }

    private ServiceOrderModel toJpaEntity(ServiceOrder serviceOrder) {
        CustomerModel customerRef = new CustomerModel();
        customerRef.setId(serviceOrder.getCustomerId());

        VehicleModel vehicleRef = new VehicleModel();
        vehicleRef.setId(serviceOrder.getVehicleId());

        return new ServiceOrderModel(
                serviceOrder.getId(),
                vehicleRef,
                customerRef,
                serviceOrder.getOrderStatus(),
                serviceOrder.getCreatedByUserId(),
                serviceOrder.getResponsibleUserId(),
                serviceOrder.getOrderNumber(),
                serviceOrder.getRequestDescription(),
                serviceOrder.getBudget(),
                serviceOrder.isHasStockPending(),
                serviceOrder.getEstimatedCompletionAt(),
                serviceOrder.getOpenedAt(),
                serviceOrder.getCompletedAt(),
                serviceOrder.getDeliveredAt(),
                serviceOrder.getCreatedAt(),
                serviceOrder.getUpdatedAt()
        );
    }

    private ServiceOrder toDomainEntity(ServiceOrderModel entity) {
        return new ServiceOrder(
                entity.getId(),
                entity.getVehicle().getId(),
                entity.getCustomer().getId(),
                entity.getOrderStatus(),
                entity.getCreatedByUserId(),
                entity.getResponsibleUserId(),
                entity.getOrderNumber(),
                entity.getRequestDescription(),
                entity.getBudget(),
                entity.isHasStockPending(),
                entity.getEstimatedCompletionAt(),
                entity.getOpenedAt(),
                entity.getCompletedAt(),
                entity.getDeliveredAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
