CREATE TABLE order_tasks (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    service_order_id UUID NOT NULL REFERENCES service_orders(id) ON DELETE RESTRICT,
    service_id UUID NOT NULL REFERENCES services(id) ON DELETE RESTRICT,
    service_status VARCHAR(100) NOT NULL,
    started_at TIMESTAMP,
    finished_at TIMESTAMP
);

CREATE INDEX idx_order_tasks_service_order_id ON order_tasks(service_order_id);
CREATE INDEX idx_order_tasks_service_id ON order_tasks(service_id);
CREATE INDEX idx_order_tasks_service_status ON order_tasks(service_status);

