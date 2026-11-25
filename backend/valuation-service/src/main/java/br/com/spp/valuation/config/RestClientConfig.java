package br.com.spp.valuation.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Disponibiliza um {@link RestClient.Builder} para os clientes HTTP de
 * comunicação entre serviços (property/market). No Spring Boot 4 o builder
 * não é auto-configurado pelo starter web, então o fornecemos explicitamente.
 */
@Configuration
public class RestClientConfig {

    @Bean
    @ConditionalOnMissingBean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
}
