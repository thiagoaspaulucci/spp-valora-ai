package br.com.spp.valuation.dto;

import br.com.spp.valuation.entity.ValuationMethod;
import br.com.spp.valuation.entity.ValuationStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Resultado de uma avaliação.
 */
@Schema(description = "Avaliação de imóvel")
public record ValuationResponse(

        UUID id,
        UUID propertyId,
        String requestedBy,
        ValuationMethod method,
        BigDecimal estimatedValue,
        BigDecimal pricePerSqm,
        BigDecimal areaSqm,
        String propertyType,
        String city,
        String state,
        ValuationStatus status,
        String notes,
        Instant createdAt
) {
}
