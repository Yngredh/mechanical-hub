ALTER TABLE users ADD COLUMN document_number VARCHAR(11);

COMMENT ON COLUMN users.document_number IS
    'Documento do funcionario (CPF), somente digitos. Usado como identificador de login pela funcao serverless de autenticacao.';
