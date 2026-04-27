package com.fiap.mechanical_hub.application.repositories;

import com.fiap.mechanical_hub.domain.entities.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceRepository {
    Optional<Service> findById(UUID id);
    Service save(Service service);
    List<Service> findAll();
    void deleteById(UUID id);
    List<Service> findByIds(List<UUID> serviceIds);
}
