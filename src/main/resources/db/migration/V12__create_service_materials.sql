CREATE TABLE service_materials (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    service_id UUID NOT NULL REFERENCES services(id) ON DELETE RESTRICT,
    material_id UUID NOT NULL REFERENCES materials(id) ON DELETE RESTRICT,
    quantity INTEGER NOT NULL
);

CREATE INDEX idx_service_materials_service_id ON service_materials(service_id);
CREATE INDEX idx_service_materials_material_id ON service_materials(material_id);
CREATE UNIQUE INDEX idx_service_materials_composite ON service_materials(service_id, material_id);

