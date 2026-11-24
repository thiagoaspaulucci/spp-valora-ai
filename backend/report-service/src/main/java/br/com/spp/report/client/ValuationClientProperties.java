package br.com.spp.report.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuração de acesso ao valuation-service. Prefixo {@code spp.valuation-service}.
 */
@ConfigurationProperties(prefix = "spp.valuation-service")
public record ValuationClientProperties(

        String uri
) {
}
