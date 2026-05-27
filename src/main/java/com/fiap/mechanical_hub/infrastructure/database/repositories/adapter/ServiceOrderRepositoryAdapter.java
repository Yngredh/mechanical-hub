package com.fiap.mechanical_hub.infrastructure.database.repositories.adapter;

import com.fiap.mechanical_hub.domain.repositories.ServiceOrderRepository;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.infrastructure.database.mappers.ServiceOrderRepositoryMapper;
import com.fiap.mechanical_hub.infrastructure.database.models.ServiceOrderModel;
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
    public ServiceOrder save(ServiceOrder order) {
        ServiceOrderModel entity = ServiceOrderRepositoryMapper.toJpaEntity(order);
        ServiceOrderModel saved = jpaRepository.save(entity);
        return ServiceOrderRepositoryMapper.toDomainEntity(saved);
    }
    @Override
    public Optional<String> findLastOrderNumberByYearMonth(String yearMonth) {
        return jpaRepository.findLastOrderNumberByYearMonth(yearMonth);
    }

    @Override
    public Optional<ServiceOrder> findById(UUID id) {
        return jpaRepository.findById(id).map(ServiceOrderRepositoryMapper::toDomainEntity);
    }

    @Override
    public List<ServiceOrder> findAll() {
        return jpaRepository.findAll().stream()
                .map(ServiceOrderRepositoryMapper::toDomainEntity)
                .toList();
    }

    public List<ServiceOrder> findAllIn(List<UUID> orderIds) {
        return jpaRepository.findAllIn(orderIds).stream()
                .map(ServiceOrderRepositoryMapper::toDomainEntity)
                .toList();
    }

    @Override
    public List<ServiceOrder> findAllByOrderByCreatedAtDesc() {
        return jpaRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(ServiceOrderRepositoryMapper::toDomainEntity)
                .toList();
    }

    @Override
    public Optional<ServiceOrder> findByOrderNumber(String orderNumber) {
        return jpaRepository.findByOrderNumber(orderNumber)
                .map(ServiceOrderRepositoryMapper::toDomainEntity);
    }

}