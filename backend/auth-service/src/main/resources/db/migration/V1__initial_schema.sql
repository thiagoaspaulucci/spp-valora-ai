-- ============================================================================
-- V1 :: Schema inicial do auth-service
-- Alinhado à entidade br.com.spp.auth.entity.User
-- (spring.jpa.hibernate.ddl-auto=validate exige correspondência exata)
-- ============================================================================

CREATE TABLE users
(
    id    UUID         NOT NULL DEFAULT gen_random_uuid(),
    name  VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,

    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email)
);
