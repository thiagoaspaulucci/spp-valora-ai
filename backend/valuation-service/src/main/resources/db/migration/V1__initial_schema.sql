-- ============================================================================
-- V1 :: Schema inicial do valuation-service (esquema "valuation")
-- Alinhado à entidade br.com.spp.valuation.entity.Valuation
-- ============================================================================

CREATE TABLE valuations
(
    id              UUID           NOT NULL DEFAULT gen_random_uuid(),
    property_id     UUID           NOT NULL,
    requested_by    VARCHAR(255)   NOT NULL,
    method          VARCHAR(30)    NOT NULL,
    estimated_value NUMERIC(15, 2) NOT NULL,
    price_per_sqm   NUMERIC(12, 2) NOT NULL,
    area_sqm        NUMERIC(12, 2) NOT NULL,
    property_type   VARCHAR(30)    NOT NULL,
    city            VARCHAR(255),
    state           VARCHAR(2),
    status          VARCHAR(20)    NOT NULL DEFAULT 'COMPLETED',
    notes           TEXT,
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),
    version         BIGINT         NOT NULL DEFAULT 0,

    CONSTRAINT pk_valuations PRIMARY KEY (id),
    CONSTRAINT chk_valuations_method CHECK (method IN ('SIMPLE_FORMULA')),
    CONSTRAINT chk_valuations_status CHECK (status IN ('COMPLETED', 'FAILED'))
);

CREATE INDEX idx_valuations_requester ON valuations (requested_by);
CREATE INDEX idx_valuations_property ON valuations (property_id);
