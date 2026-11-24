package br.com.spp.valuation.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuração de acesso ao property-service. Prefixo {@code spp.property-service}.
 */
@ConfigurationProperties(prefix = "spp.property-service")
public record PropertyClientProperties(

        String uri
) {
}
