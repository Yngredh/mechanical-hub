CREATE TABLE stock_pending_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    service_order_id UUID NOT NULL REFERENCES service_orders(id) ON DELETE RESTRICT,
    material_id UUID NOT NULL REFERENCES materials(id) ON DELETE RESTRICT,
    quantity INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_stock_pending_items_service_order_id ON stock_pending_items(service_order_id);
CREATE INDEX idx_stock_pending_items_material_id ON stock_pending_items(material_id);
CREATE INDEX idx_stock_pending_items_composite ON stock_pending_items(service_order_id, created_at);

