package com.fiap.mechanical_hub.infrastructure.database.adapter;

import com.fiap.mechanical_hub.domain.entities.ServiceData;
import com.fiap.mechanical_hub.domain.repositories.ServiceRepository;
import com.fiap.mechanical_hub.infrastructure.database.mappers.OrderTaskRepositoryMapper;
import com.fiap.mechanical_hub.infrastructure.database.mappers.ServiceRepositoryMapper;
import com.fiap.mechanical_hub.infrastructure.database.models.ServiceModel;
import com.fiap.mechanical_hub.infrastructure.database.repositories.ServiceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ServiceRepositoryAdapter implements ServiceRepository {

    private final ServiceJpaRepository jpaRepository;

    @Override
    public ServiceData save(ServiceData serviceData) {
        ServiceModel entity = OrderTaskRepositoryMapper.toModel(serviceData);
        ServiceModel saved = jpaRepository.save(entity);
        return ServiceRepositoryMapper.toDomainEntity(saved);
    }

    @Override
    public Optional<ServiceData> findById(UUID id) {
        return jpaRepository.findByIdAndDeletedAtIsNull(id).map(ServiceRepositoryMapper::toDomainEntity);
    }

    @Override
    public List<ServiceData> findAll() {
        return jpaRepository.findByDeletedAtIsNull().stream()
                .map(ServiceRepositoryMapper::toDomainEntity)
                .toList();
    }

    @Override
    public List<ServiceData> findAllIn(List<UUID> serviceIds) {
        return jpaRepository.findAllIn(serviceIds).stream()
                .map(ServiceRepositoryMapper::toDomainEntity)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.softDelete(id);
    }

}
