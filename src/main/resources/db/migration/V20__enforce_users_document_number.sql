-- Fecha o contrato da coluna document_number (§3 da spec da autenticacao).
--
-- Roda depois do backfill (V19). Falha se sobrou algum usuario sem documento,
-- o que e o comportamento desejado: melhor quebrar o deploy do que deixar um
-- funcionario sem conseguir autenticar.
DO $$
DECLARE
    missing INTEGER;
BEGIN
    SELECT COUNT(*) INTO missing FROM users WHERE document_number IS NULL;

    IF missing > 0 THEN
        RAISE EXCEPTION
            'Existem % usuario(s) sem document_number. Complete o backfill na V19 antes de aplicar esta migration.',
            missing;
    END IF;
END $$;

ALTER TABLE users ALTER COLUMN document_number SET NOT NULL;
ALTER TABLE users ADD CONSTRAINT uk_users_document_number UNIQUE (document_number);
CREATE INDEX idx_users_document_number ON users(document_number);
