package com.fiap.mechanical_hub.infrastructure.database.repositories.adapter;

import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderSummaryResponse;
import com.fiap.mechanical_hub.application.mappers.ServiceOrderMapper;
import com.fiap.mechanical_hub.application.repositories.ServiceOrderRepository;

import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.infrastructure.database.models.ServiceOrderModel;
import com.fiap.mechanical_hub.infrastructure.database.repositories.ServiceOrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ServiceOrderRepositoryAdapter implements ServiceOrderRepository {

    private final ServiceOrderJpaRepository jpaRepository;

    public List<ServiceOrderSummaryResponse> findAllSummaries(String status, UUID customerId, LocalDateTime startDate, LocalDateTime endDate){

        return jpaRepository.findAllSummaries(status, customerId, startDate, endDate);
    }

    public List<ServiceOrder> findSummaryByCustomerId(UUID customerId) {
        return jpaRepository.findSummaryByCustomerId(customerId)
                .stream()
                .map(ServiceOrderMapper::toDomainEntity)
                .toList();
    }

    @Override
    public ServiceOrder save(ServiceOrder order) {
        ServiceOrderModel entity = ServiceOrderMapper.toJpaEntity(order);
        ServiceOrderModel saved = jpaRepository.save(entity);
        return ServiceOrderMapper.toDomainEntity(saved);
    }
    @Override
    public Optional<String> findLastOrderNumberByYearMonth(String yearMonth) {
        return jpaRepository.findLastOrderNumberByYearMonth(yearMonth);
    }

    @Override
    public Optional<ServiceOrder> findById(UUID id) {
        return jpaRepository.findById(id).map(ServiceOrderMapper::toDomainEntity);
    }

    @Override
    public List<ServiceOrder> findAll() {
        return jpaRepository.findAll().stream()
                .map(ServiceOrderMapper::toDomainEntity)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<ServiceOrder> findAllByOrderByCreatedAtDesc() {
        return jpaRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(ServiceOrderMapper::toDomainEntity)
                .toList();
    }

}