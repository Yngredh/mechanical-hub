-- V15__seed_default_users.sql
-- This seed is for testing purposes
INSERT INTO users (id, name, email, password_hash, profile_id, created_at, updated_at)
VALUES
    (
        uuid_generate_v4(),
        'Administrador Padrão',
        'admin@mechanicalhub.com',
        '$2a$10$X9/XYPVGcZt6C0zRm9ncSumgLtJ2fb1QDHFcZnh0xVfNfuOEo7HI6',
        (SELECT id FROM profiles WHERE name = 'ADMINISTRATOR'),
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        uuid_generate_v4(),
        'Mecânico Padrão',
        'mecanico@mechanicalhub.com',
        '$2a$10$sDiafR.rr1XPTXd5GR1bKehHse75Hw6p.Ha9W2Mp5kv0GScc0LECe',
        (SELECT id FROM profiles WHERE name = 'MECHANICAL'),
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    )
    ON CONFLICT DO NOTHING;