package br.com.spp.market.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Preço de referência de mercado.
 */
@Schema(description = "Preço de referência de mercado")
public record MarketReferenceResponse(

        UUID id,
        String city,
        String state,
        String propertyType,
        BigDecimal pricePerSqm,
        LocalDate effectiveDate,
        String source,
        String createdBy,
        Instant createdAt,
        Instant updatedAt
) {
}
