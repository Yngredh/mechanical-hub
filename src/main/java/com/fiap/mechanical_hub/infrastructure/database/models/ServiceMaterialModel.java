package com.fiap.mechanical_hub.infrastructure.database.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "service_materials")
@Getter
@Setter
public class ServiceMaterialModel {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceModel service;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private MaterialModel material;

    @Column(nullable = false)
    private int quantity;

    public ServiceMaterialModel() {}

    public ServiceMaterialModel(UUID id, ServiceModel service, MaterialModel material, int quantity) {
        this.id = id;
        this.service = service;
        this.material = material;
        this.quantity = quantity;
    }
}