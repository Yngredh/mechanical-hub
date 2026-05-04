-- ============================================================
-- SEED – mechanical-hub
-- ============================================================

-- ------------------------------------------------------------
-- CUSTOMERS
-- ------------------------------------------------------------
INSERT INTO customers (id, name, document_type, document_number, telephone, email, address)
SELECT '96052f8d-0817-4124-84ad-918b7c9ac73d', 'Carlos Eduardo Mendes', 'CPF', '52998224725', '5511987453210', 'carlos.mendes@gmail.com', 'Rua das Flores, 123, São Paulo, SP'
    WHERE NOT EXISTS (SELECT 1 FROM customers WHERE document_number = '52998224725');

INSERT INTO customers (id, name, document_type, document_number, telephone, email, address)
SELECT '69c2954a-3606-48f4-a38b-28a50062e962', 'Transportadora Rota Livre Ltda', 'CNPJ', '11222333000181', '551133445566', 'contato@rotalivretransportes.com.br', 'Avenida dos Transportes, 456, São Paulo, SP'
    WHERE NOT EXISTS (SELECT 1 FROM customers WHERE document_number = '11222333000181');

INSERT INTO customers (id, name, document_type, document_number, telephone, email, address)
SELECT 'a6a6e27c-a061-4416-82dc-80233eda96d0', 'Salvio Silva', 'CPF', '12345678909', '551133445566', 'salvio.silva@gmail.com', 'Av. Industrial, 890, Distrito Industrial, Guarulhos - SP, 07220-000'
    WHERE NOT EXISTS (SELECT 1 FROM customers WHERE document_number = '12345678909');

-- ------------------------------------------------------------
-- VEHICLES
-- ------------------------------------------------------------
INSERT INTO vehicles (id, customer_id, license_plate, brand, model, year, color)
SELECT 'd0803851-6a5f-4fea-8463-a723be0e390c', '96052f8d-0817-4124-84ad-918b7c9ac73d', 'BRA2E19', 'Volkswagen', 'Gol 1.0', 2021, 'Branco'
    WHERE NOT EXISTS (SELECT 1 FROM vehicles WHERE license_plate = 'BRA2E19');

INSERT INTO vehicles (id, customer_id, license_plate, brand, model, year, color)
SELECT '00000000-0000-0000-0000-000000000011', '69c2954a-3606-48f4-a38b-28a50062e962', 'ABC1234', 'Chevrolet', 'S10 2.8 Turbo Diesel', 2018, 'Prata'
    WHERE NOT EXISTS (SELECT 1 FROM vehicles WHERE license_plate = 'ABC1234');

INSERT INTO vehicles (id, customer_id, license_plate, brand, model, year, color)
SELECT '241ad3cc-f878-49d3-9292-94ca1d4912d9', '69c2954a-3606-48f4-a38b-28a50062e962', 'EFG2142', 'Renault', 'Kwid', 2022, 'Branco'
    WHERE NOT EXISTS (SELECT 1 FROM vehicles WHERE license_plate = 'EFG2142');

-- ------------------------------------------------------------
-- MATERIALS
-- ------------------------------------------------------------
INSERT INTO materials (id, name, description, unit_price, min_stock_quantity)
SELECT '789492e9-c859-4ad7-8a9a-39d3ffb12519', 'Óleo de Motor 5W-30 Sintético (1L)', 'Óleo lubrificante sintético para motores flex e a gasolina, viscosidade 5W-30.', 38.90, 20
    WHERE NOT EXISTS (SELECT 1 FROM materials WHERE id = '789492e9-c859-4ad7-8a9a-39d3ffb12519');

INSERT INTO materials (id, name, description, unit_price, min_stock_quantity)
SELECT 'eb694988-23ef-413b-b68a-c510f6346534', 'Filtro de Óleo Universal', 'Filtro de óleo rosqueável compatível com motores 1.0 a 2.0 de múltiplas marcas.', 22.50, 10
    WHERE NOT EXISTS (SELECT 1 FROM materials WHERE id = 'eb694988-23ef-413b-b68a-c510f6346534');

INSERT INTO materials (id, name, description, unit_price, min_stock_quantity)
SELECT '99370d9f-1cbc-468d-8043-94588108b7c2', 'Pastilha de Freio Dianteira', 'Jogo de pastilhas de freio dianteiras com indicador de desgaste, compatível com veículos populares.', 89.90, 8
    WHERE NOT EXISTS (SELECT 1 FROM materials WHERE id = '99370d9f-1cbc-468d-8043-94588108b7c2');

INSERT INTO materials (id, name, description, unit_price, min_stock_quantity)
SELECT 'a7f744e8-2a29-41ba-85f4-6eb13317af07', 'Fluido de Freio DOT 4 (500ml)', 'Fluido de freio sintético DOT 4, alta temperatura de ebulição, para sistemas ABS e convencionais.', 32.00, 10
    WHERE NOT EXISTS (SELECT 1 FROM materials WHERE id = 'a7f744e8-2a29-41ba-85f4-6eb13317af07');

INSERT INTO materials (id, name, description, unit_price, min_stock_quantity)
SELECT '88221fb1-880c-4eb8-b0ed-be3e59d34269', 'Correia Dentada', 'Correia dentada para motores 1.0 a 1.6, fabricada em borracha reforçada com fibra de aramida.', 95.00, 5
    WHERE NOT EXISTS (SELECT 1 FROM materials WHERE id = '88221fb1-880c-4eb8-b0ed-be3e59d34269');

INSERT INTO materials (id, name, description, unit_price, min_stock_quantity)
SELECT '18c8a5ca-1d69-4690-a9c5-0877d928b96a', 'Tensor de Correia Dentada', 'Tensor automático para correia dentada, garante tensão adequada e reduz desgaste prematuro.', 75.00, 5
    WHERE NOT EXISTS (SELECT 1 FROM materials WHERE id = '18c8a5ca-1d69-4690-a9c5-0877d928b96a');

INSERT INTO materials (id, name, description, unit_price, min_stock_quantity)
SELECT 'b6313df9-9c00-4750-84ad-1af4912f9373', 'Rolamento Esticador de Correia', 'Rolamento esticador para kit de correia dentada, compatível com motores de 1.0 a 2.0.', 55.00, 5
    WHERE NOT EXISTS (SELECT 1 FROM materials WHERE id = 'b6313df9-9c00-4750-84ad-1af4912f9373');

-- ------------------------------------------------------------
-- STOCK
-- ------------------------------------------------------------
INSERT INTO stock (id, material_id, quantity, status)
SELECT uuid_generate_v4(), '789492e9-c859-4ad7-8a9a-39d3ffb12519', 5, 'AVAILABLE'
    WHERE NOT EXISTS (SELECT 1 FROM stock WHERE material_id = '789492e9-c859-4ad7-8a9a-39d3ffb12519');

INSERT INTO stock (id, material_id, quantity, status)
SELECT uuid_generate_v4(), 'eb694988-23ef-413b-b68a-c510f6346534', 5, 'AVAILABLE'
    WHERE NOT EXISTS (SELECT 1 FROM stock WHERE material_id = 'eb694988-23ef-413b-b68a-c510f6346534');

INSERT INTO stock (id, material_id, quantity, status)
SELECT uuid_generate_v4(), '99370d9f-1cbc-468d-8043-94588108b7c2', 0, 'AVAILABLE'
    WHERE NOT EXISTS (SELECT 1 FROM stock WHERE material_id = '99370d9f-1cbc-468d-8043-94588108b7c2');

INSERT INTO stock (id, material_id, quantity, status)
SELECT uuid_generate_v4(), 'a7f744e8-2a29-41ba-85f4-6eb13317af07', 0, 'AVAILABLE'
    WHERE NOT EXISTS (SELECT 1 FROM stock WHERE material_id = 'a7f744e8-2a29-41ba-85f4-6eb13317af07');

INSERT INTO stock (id, material_id, quantity, status)
SELECT uuid_generate_v4(), '88221fb1-880c-4eb8-b0ed-be3e59d34269', 3, 'AVAILABLE'
    WHERE NOT EXISTS (SELECT 1 FROM stock WHERE material_id = '88221fb1-880c-4eb8-b0ed-be3e59d34269');

INSERT INTO stock (id, material_id, quantity, status)
SELECT uuid_generate_v4(), '18c8a5ca-1d69-4690-a9c5-0877d928b96a', 1, 'AVAILABLE'
    WHERE NOT EXISTS (SELECT 1 FROM stock WHERE material_id = '18c8a5ca-1d69-4690-a9c5-0877d928b96a');

INSERT INTO stock (id, material_id, quantity, status)
SELECT uuid_generate_v4(), 'b6313df9-9c00-4750-84ad-1af4912f9373', 5, 'AVAILABLE'
    WHERE NOT EXISTS (SELECT 1 FROM stock WHERE material_id = 'b6313df9-9c00-4750-84ad-1af4912f9373');

-- ------------------------------------------------------------
-- SERVICES
-- ------------------------------------------------------------
INSERT INTO services (id, name, description, base_price, labor_cost, total_price)
SELECT '03d94934-6201-449b-b8db-490546c35d34', 'Troca de Óleo e Filtro', 'Substituição do óleo do motor e filtro de óleo conforme especificação do fabricante.', 50.00, 80.00, 308.10
    WHERE NOT EXISTS (SELECT 1 FROM services WHERE id = '03d94934-6201-449b-b8db-490546c35d34');

INSERT INTO services (id, name, description, base_price, labor_cost, total_price)
SELECT 'b9090e29-c8f8-498f-b536-bb70cb69113e', 'Manutenção de Sistema de Freios', 'Substituição de pastilhas de freio dianteiras e fluido de freio para garantir eficiência na frenagem.', 90.00, 120.00, 331.90
    WHERE NOT EXISTS (SELECT 1 FROM services WHERE id = 'b9090e29-c8f8-498f-b536-bb70cb69113e');

INSERT INTO services (id, name, description, base_price, labor_cost, total_price)
SELECT '192c4db5-61b3-4cf7-b02b-54e948a9ce5d', 'Substituição de Correia Dentada', 'Troca da correia dentada e tensores conforme intervalo recomendado pelo fabricante, prevenindo falhas no motor.', 180.00, 200.00, 605.00
    WHERE NOT EXISTS (SELECT 1 FROM services WHERE id = '192c4db5-61b3-4cf7-b02b-54e948a9ce5d');

-- ------------------------------------------------------------
-- SERVICE_MATERIALS
-- ------------------------------------------------------------
INSERT INTO service_materials (id, service_id, material_id, quantity)
SELECT 'f657b8e6-efa2-424d-beae-f4c2bb8f9201', '03d94934-6201-449b-b8db-490546c35d34', '789492e9-c859-4ad7-8a9a-39d3ffb12519', 4
    WHERE NOT EXISTS (SELECT 1 FROM service_materials WHERE service_id = '03d94934-6201-449b-b8db-490546c35d34' AND material_id = '789492e9-c859-4ad7-8a9a-39d3ffb12519');

INSERT INTO service_materials (id, service_id, material_id, quantity)
SELECT 'b9e8141c-e46c-491f-b67d-f4a3c3d8c157', '03d94934-6201-449b-b8db-490546c35d34', 'eb694988-23ef-413b-b68a-c510f6346534', 1
    WHERE NOT EXISTS (SELECT 1 FROM service_materials WHERE service_id = '03d94934-6201-449b-b8db-490546c35d34' AND material_id = 'eb694988-23ef-413b-b68a-c510f6346534');

INSERT INTO service_materials (id, service_id, material_id, quantity)
SELECT '2f5526b8-12d5-4207-928e-8edd05058dd6', 'b9090e29-c8f8-498f-b536-bb70cb69113e', '99370d9f-1cbc-468d-8043-94588108b7c2', 1
    WHERE NOT EXISTS (SELECT 1 FROM service_materials WHERE service_id = 'b9090e29-c8f8-498f-b536-bb70cb69113e' AND material_id = '99370d9f-1cbc-468d-8043-94588108b7c2');

INSERT INTO service_materials (id, service_id, material_id, quantity)
SELECT '0e64dd4b-b4c0-4d9d-ac9a-cd29f738a3ac', 'b9090e29-c8f8-498f-b536-bb70cb69113e', 'a7f744e8-2a29-41ba-85f4-6eb13317af07', 1
    WHERE NOT EXISTS (SELECT 1 FROM service_materials WHERE service_id = 'b9090e29-c8f8-498f-b536-bb70cb69113e' AND material_id = 'a7f744e8-2a29-41ba-85f4-6eb13317af07');

INSERT INTO service_materials (id, service_id, material_id, quantity)
SELECT 'b637f9aa-a7e3-44ef-a465-214e7df8453c', '192c4db5-61b3-4cf7-b02b-54e948a9ce5d', '88221fb1-880c-4eb8-b0ed-be3e59d34269', 1
    WHERE NOT EXISTS (SELECT 1 FROM service_materials WHERE service_id = '192c4db5-61b3-4cf7-b02b-54e948a9ce5d' AND material_id = '88221fb1-880c-4eb8-b0ed-be3e59d34269');

INSERT INTO service_materials (id, service_id, material_id, quantity)
SELECT '48aed2ce-2456-468e-a4d6-c7bcf5a79e10', '192c4db5-61b3-4cf7-b02b-54e948a9ce5d', '18c8a5ca-1d69-4690-a9c5-0877d928b96a', 1
    WHERE NOT EXISTS (SELECT 1 FROM service_materials WHERE service_id = '192c4db5-61b3-4cf7-b02b-54e948a9ce5d' AND material_id = '18c8a5ca-1d69-4690-a9c5-0877d928b96a');

INSERT INTO service_materials (id, service_id, material_id, quantity)
SELECT '60c9af8c-9334-427d-b35c-4ed19413965c', '192c4db5-61b3-4cf7-b02b-54e948a9ce5d', 'b6313df9-9c00-4750-84ad-1af4912f9373', 1
    WHERE NOT EXISTS (SELECT 1 FROM service_materials WHERE service_id = '192c4db5-61b3-4cf7-b02b-54e948a9ce5d' AND material_id = 'b6313df9-9c00-4750-84ad-1af4912f9373');

-- ------------------------------------------------------------
-- SERVICE_ORDERS
-- ------------------------------------------------------------
INSERT INTO service_orders (id, vehicle_id, customer_id, order_status, created_by_user_id, responsible_user_id, order_number, request_description, budget, has_stock_pending, estimated_completion_at, opened_at, completed_at, delivered_at, created_at, updated_at)
SELECT '5cce96e4-d0b2-42c1-a2b9-62fb6e97786e', 'd0803851-6a5f-4fea-8463-a723be0e390c', '96052f8d-0817-4124-84ad-918b7c9ac73d', 'RECEBIDO', (SELECT id FROM users WHERE email = 'admin@mechanicalhub.com'), NULL, 'OS-202604-0001', 'Solicita Substituição de Correia Dentada', NULL, false, NULL, CURRENT_TIMESTAMP, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM service_orders WHERE order_number = 'OS-202604-0001');

INSERT INTO service_orders (id, vehicle_id, customer_id, order_status, created_by_user_id, responsible_user_id, order_number, request_description, budget, has_stock_pending, estimated_completion_at, opened_at, completed_at, delivered_at, created_at, updated_at)
SELECT '027d61d3-2781-448a-8141-364fe9ab961c', '241ad3cc-f878-49d3-9292-94ca1d4912d9', '69c2954a-3606-48f4-a38b-28a50062e962', 'AGUARDANDO_APROVACAO', (SELECT id FROM users WHERE email = 'mecanico@mechanicalhub.com'), NULL, 'OS-0002', 'Revisão completa e ajustes', 425.00, true, NULL, CURRENT_TIMESTAMP - INTERVAL '1 hours', NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM service_orders WHERE order_number = 'OS-0002');

INSERT INTO service_orders (id, vehicle_id, customer_id, order_status, created_by_user_id, responsible_user_id, order_number, request_description, budget, has_stock_pending, estimated_completion_at, opened_at, completed_at, delivered_at, created_at, updated_at)
SELECT '4b3e8a1d-5f2c-4e9a-bc7d-1a2b3c4d5e6f', '241ad3cc-f878-49d3-9292-94ca1d4912d9', '69c2954a-3606-48f4-a38b-28a50062e962', 'AGUARDANDO_APROVACAO', (SELECT id FROM users WHERE email = 'mecanico@mechanicalhub.com'), NULL, 'OS-0003', 'Revisão completa e ajustes', 425.00, true, NULL, CURRENT_TIMESTAMP - INTERVAL '1 hours', NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM service_orders WHERE order_number = 'OS-0003');

INSERT INTO service_orders (
    id,
    vehicle_id,
    customer_id,
    order_status,
    created_by_user_id,
    responsible_user_id,
    order_number,
    request_description,
    budget,
    has_stock_pending,
    estimated_completion_at,
    opened_at,
    completed_at,
    delivered_at,
    created_at,
    updated_at
)
SELECT
    'aabbccdd-0000-0000-0000-111111111111',
    'd0803851-6a5f-4fea-8463-a723be0e390c',
    '96052f8d-0817-4124-84ad-918b7c9ac73d',
    'FINALIZADO',
    (SELECT id FROM users WHERE email = 'admin@mechanicalhub.com'),
    (SELECT id FROM users WHERE email = 'mecanico@mechanicalhub.com'),
    'OS-202604-0099',
    'Revisão completa: troca de óleo, freios e correia dentada.',
    1245.00,
    false,
    CURRENT_TIMESTAMP - INTERVAL '2 hours',
    CURRENT_TIMESTAMP - INTERVAL '5 hours',
    CURRENT_TIMESTAMP - INTERVAL '30 minutes',
    NULL,
    CURRENT_TIMESTAMP - INTERVAL '6 hours',
    CURRENT_TIMESTAMP - INTERVAL '30 minutes'
WHERE NOT EXISTS (SELECT 1 FROM service_orders WHERE order_number = 'OS-202604-0099');

-- ------------------------------------------------------------
-- ORDER_TASKS
-- ------------------------------------------------------------
INSERT INTO order_tasks (id, service_order_id, service_id, service_status, started_at, finished_at)
SELECT 'fa779610-c76d-4c81-ae65-3db26925cfdc', '027d61d3-2781-448a-8141-364fe9ab961c', '192c4db5-61b3-4cf7-b02b-54e948a9ce5d', 'PENDENTE', NULL, NULL
    WHERE NOT EXISTS (SELECT 1 FROM order_tasks WHERE id = 'fa779610-c76d-4c81-ae65-3db26925cfdc');

INSERT INTO order_tasks (id, service_order_id, service_id, service_status, started_at, finished_at)
SELECT '1b5f092e-8bf4-4f1c-b9a8-d745f4195e57', '5cce96e4-d0b2-42c1-a2b9-62fb6e97786e', '192c4db5-61b3-4cf7-b02b-54e948a9ce5d', 'PENDENTE', NULL, NULL
    WHERE NOT EXISTS (SELECT 1 FROM order_tasks WHERE id = '1b5f092e-8bf4-4f1c-b9a8-d745f4195e57');

INSERT INTO order_tasks (id, service_order_id, service_id, service_status, started_at, finished_at)
SELECT '843622e7-7c14-41bc-aad6-b41c2e7e7583', '5cce96e4-d0b2-42c1-a2b9-62fb6e97786e', '03d94934-6201-449b-b8db-490546c35d34', 'PENDENTE', CURRENT_TIMESTAMP - INTERVAL '30 minutes', NULL
WHERE NOT EXISTS (SELECT 1 FROM order_tasks WHERE id = '843622e7-7c14-41bc-aad6-b41c2e7e7583');

INSERT INTO order_tasks (id, service_order_id, service_id, service_status, started_at, finished_at)
SELECT 'd4bb7b88-b8b4-402a-bae8-039aead53188', '5cce96e4-d0b2-42c1-a2b9-62fb6e97786e', 'b9090e29-c8f8-498f-b536-bb70cb69113e', 'PENDENTE', CURRENT_TIMESTAMP - INTERVAL '30 minutes', NULL
WHERE NOT EXISTS (SELECT 1 FROM order_tasks WHERE id = 'd4bb7b88-b8b4-402a-bae8-039aead53188');

INSERT INTO order_tasks (id, service_order_id, service_id, service_status, started_at, finished_at)
SELECT
    'aabbccdd-1111-1111-1111-000000000001',
    'aabbccdd-0000-0000-0000-111111111111',
    '03d94934-6201-449b-b8db-490546c35d34',
    'FINALIZADO',
    CURRENT_TIMESTAMP - INTERVAL '4 hours 30 minutes',
    CURRENT_TIMESTAMP - INTERVAL '3 hours 30 minutes'
WHERE NOT EXISTS (SELECT 1 FROM order_tasks WHERE id = 'aabbccdd-1111-1111-1111-000000000001');

INSERT INTO order_tasks (id, service_order_id, service_id, service_status, started_at, finished_at)
SELECT
    'aabbccdd-1111-1111-1111-000000000002',
    'aabbccdd-0000-0000-0000-111111111111',
    'b9090e29-c8f8-498f-b536-bb70cb69113e',
    'FINALIZADO',
    CURRENT_TIMESTAMP - INTERVAL '3 hours 30 minutes',
    CURRENT_TIMESTAMP - INTERVAL '2 hours 30 minutes'
WHERE NOT EXISTS (SELECT 1 FROM order_tasks WHERE id = 'aabbccdd-1111-1111-1111-000000000002');

INSERT INTO order_tasks (id, service_order_id, service_id, service_status, started_at, finished_at)
SELECT
    'aabbccdd-1111-1111-1111-000000000003',
    'aabbccdd-0000-0000-0000-111111111111',
    '192c4db5-61b3-4cf7-b02b-54e948a9ce5d',
    'FINALIZADO',
    CURRENT_TIMESTAMP - INTERVAL '2 hours 30 minutes',
    CURRENT_TIMESTAMP - INTERVAL '30 minutes'
WHERE NOT EXISTS (SELECT 1 FROM order_tasks WHERE id = 'aabbccdd-1111-1111-1111-000000000003');

-- ------------------------------------------------------------
-- STOCK_PENDING_ITEMS
-- ------------------------------------------------------------
INSERT INTO stock_pending_items (id, service_order_id, material_id, quantity, created_at)
SELECT 'd97640b2-bb16-43a3-8ba6-319dc64ac04f', '5cce96e4-d0b2-42c1-a2b9-62fb6e97786e', '99370d9f-1cbc-468d-8043-94588108b7c2', 1, CURRENT_TIMESTAMP - INTERVAL '20 minutes'
WHERE NOT EXISTS (SELECT 1 FROM stock_pending_items WHERE id = 'd97640b2-bb16-43a3-8ba6-319dc64ac04f');

INSERT INTO stock_pending_items (id, service_order_id, material_id, quantity, created_at)
SELECT '1eabac34-d65d-45a7-ba64-85f0013c10ee', '5cce96e4-d0b2-42c1-a2b9-62fb6e97786e', 'a7f744e8-2a29-41ba-85f4-6eb13317af07', 1, CURRENT_TIMESTAMP - INTERVAL '20 minutes'
WHERE NOT EXISTS (SELECT 1 FROM stock_pending_items WHERE id = '1eabac34-d65d-45a7-ba64-85f0013c10ee');

-- ------------------------------------------------------------
-- STOCK_MOVEMENTS –
-- ------------------------------------------------------------

-- 4x Óleo de Motor (Troca de Óleo)
INSERT INTO stock_movements (id, material_id, service_order_id, movement_type, quantity, created_at)
SELECT
    'aabbccdd-2222-2222-2222-000000000001',
    '789492e9-c859-4ad7-8a9a-39d3ffb12519',
    'aabbccdd-0000-0000-0000-111111111111',
    'RESERVED',
    4,
    CURRENT_TIMESTAMP - INTERVAL '5 hours'
WHERE NOT EXISTS (SELECT 1 FROM stock_movements WHERE id = 'aabbccdd-2222-2222-2222-000000000001');

-- 1x Filtro de Óleo (Troca de Óleo)
INSERT INTO stock_movements (id, material_id, service_order_id, movement_type, quantity, created_at)
SELECT
    'aabbccdd-2222-2222-2222-000000000002',
    'eb694988-23ef-413b-b68a-c510f6346534',
    'aabbccdd-0000-0000-0000-111111111111',
    'RESERVED',
    1,
    CURRENT_TIMESTAMP - INTERVAL '5 hours'
WHERE NOT EXISTS (SELECT 1 FROM stock_movements WHERE id = 'aabbccdd-2222-2222-2222-000000000002');

-- 1x Pastilha de Freio (Freios)
INSERT INTO stock_movements (id, material_id, service_order_id, movement_type, quantity, created_at)
SELECT
    'aabbccdd-2222-2222-2222-000000000003',
    '99370d9f-1cbc-468d-8043-94588108b7c2',
    'aabbccdd-0000-0000-0000-111111111111',
    'RESERVED',
    1,
    CURRENT_TIMESTAMP - INTERVAL '5 hours'
WHERE NOT EXISTS (SELECT 1 FROM stock_movements WHERE id = 'aabbccdd-2222-2222-2222-000000000003');

-- 1x Fluido de Freio DOT 4 (Freios)
INSERT INTO stock_movements (id, material_id, service_order_id, movement_type, quantity, created_at)
SELECT
    'aabbccdd-2222-2222-2222-000000000004',
    'a7f744e8-2a29-41ba-85f4-6eb13317af07',
    'aabbccdd-0000-0000-0000-111111111111',
    'RESERVED',
    1,
    CURRENT_TIMESTAMP - INTERVAL '5 hours'
WHERE NOT EXISTS (SELECT 1 FROM stock_movements WHERE id = 'aabbccdd-2222-2222-2222-000000000004');

-- 1x Correia Dentada (Correia)
INSERT INTO stock_movements (id, material_id, service_order_id, movement_type, quantity, created_at)
SELECT
    'aabbccdd-2222-2222-2222-000000000005',
    '88221fb1-880c-4eb8-b0ed-be3e59d34269',
    'aabbccdd-0000-0000-0000-111111111111',
    'RESERVED',
    1,
    CURRENT_TIMESTAMP - INTERVAL '5 hours'
WHERE NOT EXISTS (SELECT 1 FROM stock_movements WHERE id = 'aabbccdd-2222-2222-2222-000000000005');

-- 1x Tensor de Correia (Correia)
INSERT INTO stock_movements (id, material_id, service_order_id, movement_type, quantity, created_at)
SELECT
    'aabbccdd-2222-2222-2222-000000000006',
    '18c8a5ca-1d69-4690-a9c5-0877d928b96a',
    'aabbccdd-0000-0000-0000-111111111111',
    'RESERVED',
    1,
    CURRENT_TIMESTAMP - INTERVAL '5 hours'
WHERE NOT EXISTS (SELECT 1 FROM stock_movements WHERE id = 'aabbccdd-2222-2222-2222-000000000006');

-- 1x Rolamento Esticador (Correia)
INSERT INTO stock_movements (id, material_id, service_order_id, movement_type, quantity, created_at)
SELECT
    'aabbccdd-2222-2222-2222-000000000007',
    'b6313df9-9c00-4750-84ad-1af4912f9373',
    'aabbccdd-0000-0000-0000-111111111111',
    'RESERVED',
    1,
    CURRENT_TIMESTAMP - INTERVAL '5 hours'
WHERE NOT EXISTS (SELECT 1 FROM stock_movements WHERE id = 'aabbccdd-2222-2222-2222-000000000007');