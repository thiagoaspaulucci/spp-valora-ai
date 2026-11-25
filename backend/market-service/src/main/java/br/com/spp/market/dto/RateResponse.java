package br.com.spp.market.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Preço de referência vigente retornado pela consulta de taxa.
 */
@Schema(description = "Preço de referência vigente")
public record RateResponse(

        String city,
        String state,
        String propertyType,
        BigDecimal pricePerSqm,
        LocalDate effectiveDate,
        String source
) {
}
