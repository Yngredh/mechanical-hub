-- Backfill dos usuarios criados antes da Fase 3.
--
-- Cobre os usuarios do seed (V15). Se o ambiente tiver funcionarios cadastrados
-- fora do seed, adicione os UPDATEs correspondentes aqui antes de aplicar a
-- V20 -- ela falha de proposito se alguem ficar sem documento, para que a
-- inconsistencia apareca no deploy e nao no primeiro login.

UPDATE users
   SET document_number = '52998224725'
 WHERE email = 'admin@mechanicalhub.com'
   AND document_number IS NULL;

UPDATE users
   SET document_number = '11144477735'
 WHERE email = 'mecanico@mechanicalhub.com'
   AND document_number IS NULL;
