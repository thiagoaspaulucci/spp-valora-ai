package br.com.spp.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Política de proteção contra força bruta no login.
 * Prefixo {@code spp.security.login}.
 */
@ConfigurationProperties(prefix = "spp.security.login")
public record LoginProperties(

        int maxAttempts,

        Duration blockDuration
) {
}
