package br.com.spp.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Origens permitidas para CORS no gateway. Prefixo {@code spp.security.cors}.
 */
@ConfigurationProperties(prefix = "spp.security.cors")
public record CorsProperties(

        List<String> allowedOrigins
) {
}
