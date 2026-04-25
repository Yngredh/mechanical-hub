package com.fiap.mechanical_hub.infrastructure.database.repositories.adapter;

import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.enums.OrderStatus;
import com.fiap.mechanical_hub.domain.repositories.ServiceOrderRepository;
import com.fiap.mechanical_hub.infrastructure.database.models.ServiceOrderModel;
import com.fiap.mechanical_hub.infrastructure.database.repositories.ServiceOrderJpaRepository;
import com.fiap.mechanical_hub.infrastructure.database.repositories.ServiceOrderSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ServiceOrderRepositoryAdapter implements ServiceOrderRepository {

    private final ServiceOrderJpaRepository jpaRepository;

    @Override
    public Optional<ServiceOrder> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomainEntity);
    }

    @Override
    public ServiceOrder save(ServiceOrder order) {
        ServiceOrderModel entity = toJpaEntity(order);
        ServiceOrderModel saved = jpaRepository.save(entity);
        return toDomainEntity(saved);
    }

    @Override
    public List<ServiceOrder> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomainEntity)
                .toList();
    }

    @Override
    public List<ServiceOrder> findAllFiltered(String status, UUID customerId, LocalDateTime startDate, LocalDateTime endDate) {
        Specification<ServiceOrderModel> spec = Specification.where(null);

        if (status != null && !status.isEmpty()) {
            spec = spec.and(ServiceOrderSpecification.hasStatus(status));
        }

        if (customerId != null) {
            spec = spec.and(ServiceOrderSpecification.hasCustomerId(customerId));
        }

        if (startDate != null || endDate != null) {
            spec = spec.and(ServiceOrderSpecification.createdBetween(startDate, endDate));
        }

        return jpaRepository.findAll(spec).stream()
                .map(this::toDomainEntity)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private ServiceOrderModel toJpaEntity(ServiceOrder order) {
        return new ServiceOrderModel(
                order.getId(),
                order.getVehicleId(),
                order.getCustomerId(),
                order.getStatus().name(),
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
                order.getUpdatedAt()
        );
    }

    private ServiceOrder toDomainEntity(ServiceOrderModel entity) {
        ServiceOrder order = new ServiceOrder();
        order.setId(entity.getId());
        order.setVehicleId(entity.getVehicleId());
        order.setCustomerId(entity.getCustomerId());
        order.setStatus(OrderStatus.fromString(entity.getOrderStatus()));
        order.setCreatedByUserId(entity.getCreatedByUserId());
        order.setResponsibleUserId(entity.getResponsibleUserId());
        order.setOrderNumber(entity.getOrderNumber());
        order.setRequestDescription(entity.getRequestDescription());
        order.setBudget(entity.getBudget());
        order.setHasStockPending(entity.isHasStockPending());
        order.setEstimatedCompletionAt(entity.getEstimatedCompletionAt());
        order.setOpenedAt(entity.getOpenedAt());
        order.setCompletedAt(entity.getCompletedAt());
        order.setDeliveredAt(entity.getDeliveredAt());
        order.setCreatedAt(entity.getCreatedAt());
        order.setUpdatedAt(entity.getUpdatedAt());
        return order;
    }
}
