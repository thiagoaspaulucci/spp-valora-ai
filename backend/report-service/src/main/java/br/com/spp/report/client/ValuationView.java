package br.com.spp.report.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Subconjunto da avaliação retornada pelo valuation-service, usado no laudo.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ValuationView(

        UUID id,
        UUID propertyId,
        BigDecimal estimatedValue,
        BigDecimal pricePerSqm,
        BigDecimal areaSqm,
        String propertyType,
        String city,
        String state,
        String method
) {
}
