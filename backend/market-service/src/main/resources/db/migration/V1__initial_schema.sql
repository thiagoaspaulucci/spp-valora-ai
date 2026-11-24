-- ============================================================================
-- V1 :: Schema inicial do market-service (esquema "market")
-- Alinhado à entidade br.com.spp.market.entity.MarketReference
-- ============================================================================

CREATE TABLE market_references
(
    id            UUID           NOT NULL DEFAULT gen_random_uuid(),
    city          VARCHAR(255)   NOT NULL,
    state         VARCHAR(2)     NOT NULL,
    property_type VARCHAR(30)    NOT NULL,
    price_per_sqm NUMERIC(12, 2) NOT NULL,
    effective_date DATE          NOT NULL,
    source        VARCHAR(255),
    created_by    VARCHAR(255)   NOT NULL,
    created_at    TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ,
    version       BIGINT         NOT NULL DEFAULT 0,

    CONSTRAINT pk_market_references PRIMARY KEY (id)
);

CREATE INDEX idx_market_lookup
    ON market_references (city, state, property_type, effective_date);
