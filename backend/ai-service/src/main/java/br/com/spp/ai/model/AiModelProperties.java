package br.com.spp.ai.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Parâmetros do modelo de estimativa. Prefixo {@code spp.ai}.
 */
@ConfigurationProperties(prefix = "spp.ai")
public record AiModelProperties(

        String modelVersion,

        BigDecimal basePricePerSqm,

        /** Pesos por tipo de imóvel. */
        Map<String, BigDecimal> typeWeights
) {
    public boolean knowsType(String type) {
        return typeWeights != null && type != null && typeWeights.containsKey(type);
    }

    public BigDecimal weightFor(String type) {
        if (typeWeights == null || type == null) {
            return BigDecimal.ONE;
        }
        return typeWeights.getOrDefault(type, BigDecimal.ONE);
    }
}
