-- ============================================================================
-- V1 :: Schema inicial do report-service (esquema "report")
-- Alinhado à entidade br.com.spp.report.entity.Report
-- ============================================================================

CREATE TABLE reports
(
    id              UUID           NOT NULL DEFAULT gen_random_uuid(),
    valuation_id    UUID           NOT NULL,
    property_id     UUID           NOT NULL,
    requested_by    VARCHAR(255)   NOT NULL,
    title           VARCHAR(500)   NOT NULL,
    property_type   VARCHAR(30)    NOT NULL,
    city            VARCHAR(255),
    state           VARCHAR(2),
    area_sqm        NUMERIC(12, 2) NOT NULL,
    price_per_sqm   NUMERIC(12, 2) NOT NULL,
    estimated_value NUMERIC(15, 2) NOT NULL,
    summary         TEXT           NOT NULL,
    status          VARCHAR(20)    NOT NULL DEFAULT 'GENERATED',
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),
    version         BIGINT         NOT NULL DEFAULT 0,

    CONSTRAINT pk_reports PRIMARY KEY (id),
    CONSTRAINT chk_reports_status CHECK (status IN ('GENERATED'))
);

CREATE INDEX idx_reports_requester ON reports (requested_by);
CREATE INDEX idx_reports_valuation ON reports (valuation_id);
