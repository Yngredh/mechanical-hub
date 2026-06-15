package com.fiap.mechanical_hub.infrastructure.database.adapter;

import com.fiap.mechanical_hub.domain.repositories.ServiceMaterialRepository;
import com.fiap.mechanical_hub.domain.entities.ServiceData;
import com.fiap.mechanical_hub.domain.entities.ServiceMaterial;
import com.fiap.mechanical_hub.infrastructure.database.mappers.OrderTaskRepositoryMapper;
import com.fiap.mechanical_hub.infrastructure.database.mappers.ServiceMaterialRepositoryMapper;
import com.fiap.mechanical_hub.infrastructure.database.models.ServiceMaterialModel;
import com.fiap.mechanical_hub.infrastructure.database.repositories.ServiceMaterialJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ServiceMaterialRepositoryAdapter implements ServiceMaterialRepository {

    private final ServiceMaterialJpaRepository jpaRepository;

    public ServiceMaterialRepositoryAdapter(
            ServiceMaterialJpaRepository jpaRepository
    ) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ServiceMaterial save(ServiceMaterial entity, ServiceData serviceData) {
        ServiceMaterialModel saved = jpaRepository.save(
                ServiceMaterialRepositoryMapper.toJpaEntity(entity, OrderTaskRepositoryMapper.toModel(serviceData)));
        return ServiceMaterialRepositoryMapper.toDomainEntity(saved);
    }

    @Override
    public List<ServiceMaterial> findByServiceId(UUID serviceId) {

        return jpaRepository
                .findByServiceId(serviceId)
                .stream()
                .map(ServiceMaterialRepositoryMapper::toDomainEntity)
                .toList();
    }

    @Override
    public List<ServiceMaterial> findByMaterialId(UUID materialId) {
        return jpaRepository
                .findByMaterialId(materialId)
                .stream()
                .map(ServiceMaterialRepositoryMapper::toDomainEntity)
                .toList();
    }

}