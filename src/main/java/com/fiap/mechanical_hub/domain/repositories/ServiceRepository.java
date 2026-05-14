package com.fiap.mechanical_hub.domain.repositories;

import com.fiap.mechanical_hub.domain.entities.ServiceData;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceRepository {

    Optional<ServiceData> findById(UUID id);

    ServiceData save(ServiceData serviceData);

    List<ServiceData> findAll();

    void deleteById(UUID id);

    List<ServiceData> findByIds(List<UUID> serviceIds);

}
