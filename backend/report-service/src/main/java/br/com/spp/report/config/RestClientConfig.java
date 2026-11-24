package br.com.spp.report.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Disponibiliza um {@link RestClient.Builder} para o cliente HTTP do
 * valuation-service. No Spring Boot 4 o builder não é auto-configurado pelo
 * starter web, então o fornecemos explicitamente.
 */
@Configuration
public class RestClientConfig {

    @Bean
    @ConditionalOnMissingBean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
}
