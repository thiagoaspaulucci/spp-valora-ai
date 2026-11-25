package br.com.spp.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Estimativa assistida por IA.
 */
@Schema(description = "Estimativa assistida por IA")
public record AiEstimateResponse(

        UUID id,
        UUID propertyId,
        String requestedBy,
        BigDecimal estimatedValue,
        BigDecimal confidence,
        String modelVersion,
        String factors,
        String propertyType,
        BigDecimal areaSqm,
        String city,
        String state,
        Instant createdAt
) {
}
