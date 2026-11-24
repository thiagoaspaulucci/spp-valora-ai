-- ============================================================================
-- V1 :: Schema inicial do property-service (esquema "property")
-- Alinhado à entidade br.com.spp.property.entity.Property
-- ============================================================================

CREATE TABLE properties
(
    id            UUID           NOT NULL DEFAULT gen_random_uuid(),
    title         VARCHAR(255)   NOT NULL,
    type          VARCHAR(30)    NOT NULL,
    street        VARCHAR(255)   NOT NULL,
    street_number VARCHAR(30),
    complement    VARCHAR(255),
    neighborhood  VARCHAR(255),
    city          VARCHAR(255)   NOT NULL,
    state         VARCHAR(2)     NOT NULL,
    zip_code      VARCHAR(20)    NOT NULL,
    area_sqm      NUMERIC(12, 2) NOT NULL,
    bedrooms      INTEGER        NOT NULL DEFAULT 0,
    bathrooms     INTEGER        NOT NULL DEFAULT 0,
    parking_spots INTEGER        NOT NULL DEFAULT 0,
    year_built    INTEGER,
    asking_price  NUMERIC(15, 2),
    status        VARCHAR(20)    NOT NULL DEFAULT 'DRAFT',
    owner_subject VARCHAR(255)   NOT NULL,
    created_at    TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ,
    version       BIGINT         NOT NULL DEFAULT 0,

    CONSTRAINT pk_properties PRIMARY KEY (id),
    CONSTRAINT chk_properties_type CHECK (type IN ('APARTMENT', 'HOUSE', 'COMMERCIAL', 'LAND')),
    CONSTRAINT chk_properties_status CHECK (status IN ('DRAFT', 'ACTIVE', 'ARCHIVED'))
);

CREATE INDEX idx_properties_owner ON properties (owner_subject);
CREATE INDEX idx_properties_city_state ON properties (city, state);
