package br.com.spp.valuation.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

/**
 * Preço de referência vigente retornado pelo market-service.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RateView(

        BigDecimal pricePerSqm
) {
}
