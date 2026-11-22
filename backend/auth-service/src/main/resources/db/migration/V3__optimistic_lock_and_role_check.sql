-- ============================================================================
-- V3 :: Controle de concorrência otimista + integridade dos papéis
-- ============================================================================

-- Coluna de versionamento (@Version)
ALTER TABLE users
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

-- Restringe os valores de 'role' aos papéis conhecidos (enum Role).
-- Ao adicionar novos papéis, atualizar esta constraint em nova migration.
ALTER TABLE user_roles
    ADD CONSTRAINT chk_user_roles_role CHECK (role IN ('ROLE_USER', 'ROLE_ADMIN'));
