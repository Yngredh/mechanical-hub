-- Fase 3 / ADR-0001: a autenticacao dos funcionarios passa a ser por CPF.
-- A coluna nasce anulavel de proposito: os usuarios ja cadastrados precisam ser
-- preenchidos (V19) antes de a restricao NOT NULL entrar (V20).
--
-- Nome neutro no modelo de dados; "CPF" e o termo de negocio e vive na API.
ALTER TABLE users ADD COLUMN document_number VARCHAR(11);

COMMENT ON COLUMN users.document_number IS
    'Documento do funcionario (CPF), somente digitos. Usado como identificador de login pela funcao serverless de autenticacao.';
