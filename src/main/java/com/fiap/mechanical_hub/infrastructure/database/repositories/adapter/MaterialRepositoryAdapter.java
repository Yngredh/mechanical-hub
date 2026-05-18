package com.fiap.mechanical_hub.infrastructure.database.repositories.adapter;

import com.fiap.mechanical_hub.domain.entities.Material;
import com.fiap.mechanical_hub.domain.repositories.MaterialRepository;
import com.fiap.mechanical_hub.infrastructure.database.mappers.MaterialRepositoryMapper;
import com.fiap.mechanical_hub.infrastructure.database.models.MaterialModel;
import com.fiap.mechanical_hub.infrastructure.database.repositories.MaterialJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.fiap.mechanical_hub.infrastructure.database.mappers.MaterialRepositoryMapper.toDomainEntity;
import static com.fiap.mechanical_hub.infrastructure.database.mappers.MaterialRepositoryMapper.toJpaEntity;

@Component
@RequiredArgsConstructor
public class MaterialRepositoryAdapter implements MaterialRepository {

    private final MaterialJpaRepository jpaRepository;

    @Override
    public Material save(Material material) {
        MaterialModel entity = toJpaEntity(material);
        MaterialModel saved = jpaRepository.save(entity);
        return toDomainEntity(saved);
    }
    @Override
    public Optional<Material> findById(UUID id) {
        return jpaRepository.findById(id).map(MaterialRepositoryMapper::toDomainEntity);
    }

    @Override
    public List<Material> findAll() {
        return jpaRepository.findAll().stream()
                .map(MaterialRepositoryMapper::toDomainEntity)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

}

