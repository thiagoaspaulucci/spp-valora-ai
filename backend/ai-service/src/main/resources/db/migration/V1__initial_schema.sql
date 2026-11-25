-- ============================================================================
-- V1 :: Schema inicial do ai-service (esquema "ai")
-- Alinhado à entidade br.com.spp.ai.entity.AiEstimate
-- ============================================================================

CREATE TABLE ai_estimates
(
    id              UUID           NOT NULL DEFAULT gen_random_uuid(),
    property_id     UUID           NOT NULL,
    requested_by    VARCHAR(255)   NOT NULL,
    estimated_value NUMERIC(15, 2) NOT NULL,
    confidence      NUMERIC(4, 3)  NOT NULL,
    model_version   VARCHAR(50)    NOT NULL,
    factors         TEXT           NOT NULL,
    property_type   VARCHAR(30)    NOT NULL,
    area_sqm        NUMERIC(12, 2) NOT NULL,
    city            VARCHAR(255),
    state           VARCHAR(2),
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),
    version         BIGINT         NOT NULL DEFAULT 0,

    CONSTRAINT pk_ai_estimates PRIMARY KEY (id),
    CONSTRAINT chk_ai_confidence CHECK (confidence >= 0 AND confidence <= 1)
);

CREATE INDEX idx_ai_requester ON ai_estimates (requested_by);
CREATE INDEX idx_ai_property ON ai_estimates (property_id);
