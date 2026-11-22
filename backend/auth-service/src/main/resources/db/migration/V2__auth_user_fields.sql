-- ============================================================================
-- V2 :: Campos de autenticação/autorização do usuário
-- Adiciona senha, flag de habilitação, auditoria e papéis (roles).
-- ============================================================================

ALTER TABLE users
    ADD COLUMN password   VARCHAR(255) NOT NULL DEFAULT '',
    ADD COLUMN enabled    BOOLEAN      NOT NULL DEFAULT TRUE,
    ADD COLUMN created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    ADD COLUMN updated_at TIMESTAMPTZ;

-- Remove o default de 'password': novos registros devem informar o hash.
ALTER TABLE users
    ALTER COLUMN password DROP DEFAULT;

CREATE TABLE user_roles
(
    user_id UUID        NOT NULL,
    role    VARCHAR(50) NOT NULL,

    CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_user_roles_user_id ON user_roles (user_id);
