package com.fiap.mechanical_hub.infrastructure.database.repositories.adapter;
import com.fiap.mechanical_hub.application.mappers.ServiceMapper;
import com.fiap.mechanical_hub.application.mappers.ServiceMaterialMapper;
import com.fiap.mechanical_hub.domain.repositories.ServiceMaterialRepository;
import com.fiap.mechanical_hub.domain.entities.ServiceData;
import com.fiap.mechanical_hub.domain.entities.ServiceMaterial;
import com.fiap.mechanical_hub.infrastructure.database.models.ServiceMaterialModel;
import com.fiap.mechanical_hub.infrastructure.database.repositories.ServiceMaterialJpaRepository;
import static com.fiap.mechanical_hub.application.mappers.ServiceMaterialMapper.toDomainEntity;
import static com.fiap.mechanical_hub.application.mappers.ServiceMaterialMapper.toJpaEntity;
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
        ServiceMaterialModel saved = jpaRepository.save(toJpaEntity(entity, ServiceMapper.toJpaEntity(serviceData)));
        return toDomainEntity(saved);
    }

    @Override
    public List<ServiceMaterial> findByServiceId(UUID serviceId) {

        return jpaRepository
                .findByServiceId(serviceId)
                .stream()
                .map(ServiceMaterialMapper::toDomainEntity)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }




}