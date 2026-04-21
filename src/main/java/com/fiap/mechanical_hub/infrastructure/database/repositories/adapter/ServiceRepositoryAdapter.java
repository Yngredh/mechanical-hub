package com.fiap.mechanical_hub.infrastructure.database.repositories.adapter;

import com.fiap.mechanical_hub.application.mappers.ServiceMapper;
import com.fiap.mechanical_hub.domain.entities.Service;
import com.fiap.mechanical_hub.domain.repositories.ServiceRepository;
import com.fiap.mechanical_hub.infrastructure.database.models.ServiceModel;
import com.fiap.mechanical_hub.infrastructure.database.repositories.ServiceJpaRepository;
import com.fiap.mechanical_hub.infrastructure.database.repositories.ServiceMaterialJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.fiap.mechanical_hub.application.mappers.ServiceMapper.toDomainEntity;
import static com.fiap.mechanical_hub.application.mappers.ServiceMapper.toJpaEntity;

@Component
@RequiredArgsConstructor
public class ServiceRepositoryAdapter implements ServiceRepository {

    private final ServiceJpaRepository jpaRepository;
    private final ServiceMaterialJpaRepository serviceMaterialJpaRepository;

    @Override
    public Service save(Service service) {
        if (service.getId() != null) {
            Optional<ServiceModel> existingModel = jpaRepository.findById(service.getId());
            if (existingModel.isPresent()) {
                ServiceModel existing = existingModel.get();
                existing.getMaterials().clear();
                jpaRepository.flush();
            }
        }

        ServiceModel entity = toJpaEntity(service);
        ServiceModel saved = jpaRepository.save(entity);
        return toDomainEntity(saved);
    }

    @Override
    public Optional<Service> findById(UUID id) {
        return jpaRepository.findById(id).map(ServiceMapper::toDomainEntity);
    }

    @Override
    public List<Service> findAll() {
        return jpaRepository.findAll().stream()
                .map(ServiceMapper::toDomainEntity)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}



