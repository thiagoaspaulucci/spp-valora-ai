package br.com.spp.valuation.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuração de acesso ao market-service. Prefixo {@code spp.market-service}.
 */
@ConfigurationProperties(prefix = "spp.market-service")
public record MarketClientProperties(

        String uri
) {
}
