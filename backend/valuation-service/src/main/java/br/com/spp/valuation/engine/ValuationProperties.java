package br.com.spp.valuation.engine;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Parâmetros da fórmula de valoração. Prefixo {@code spp.valuation}.
 */
@ConfigurationProperties(prefix = "spp.valuation")
public record ValuationProperties(

        /** Preço base por m² (antes do multiplicador de tipo). */
        BigDecimal basePricePerSqm,

        /** Multiplicador por tipo de imóvel (APARTMENT, HOUSE, COMMERCIAL, LAND). */
        Map<String, BigDecimal> typeMultipliers
) {
    public BigDecimal multiplierFor(String type) {
        if (typeMultipliers == null || type == null) {
            return BigDecimal.ONE;
        }
        return typeMultipliers.getOrDefault(type, BigDecimal.ONE);
    }
}
