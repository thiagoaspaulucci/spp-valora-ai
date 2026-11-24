package br.com.spp.valuation.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Subconjunto dos dados do imóvel retornados pelo property-service,
 * necessários para a avaliação.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PropertyView(

        UUID id,
        String type,
        BigDecimal areaSqm,
        int bedrooms,
        int bathrooms,
        int parkingSpots,
        Integer yearBuilt,
        String city,
        String state
) {
}
