package com.fiap.mechanical_hub.domain.repositories;

import com.fiap.mechanical_hub.domain.entities.Material;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MaterialRepository {

    Material save(Material material);

    Optional<Material> findById(UUID id);

    List<Material> findAll();

    void deleteById(UUID id);

}

