package br.com.spp.report.dto;

import br.com.spp.report.entity.ReportStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Laudo de avaliação.
 */
@Schema(description = "Laudo de avaliação")
public record ReportResponse(

        UUID id,
        UUID valuationId,
        UUID propertyId,
        String requestedBy,
        String title,
        String propertyType,
        String city,
        String state,
        BigDecimal areaSqm,
        BigDecimal pricePerSqm,
        BigDecimal estimatedValue,
        String summary,
        ReportStatus status,
        Instant createdAt
) {
}
