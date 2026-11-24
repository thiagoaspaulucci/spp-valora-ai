package br.com.spp.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

import java.time.Duration;

/**
 * Parâmetros de emissão e assinatura dos tokens JWT.
 * Prefixo {@code spp.security.jwt}.
 *
 * <p>Quando {@code publicKeyLocation} e {@code privateKeyLocation} são
 * informados, as chaves RSA são carregadas desses recursos (produção).
 * Caso contrário, um par efêmero é gerado na inicialização (apenas dev).</p>
 */
@ConfigurationProperties(prefix = "spp.security.jwt")
public record JwtProperties(

        String issuer,

        Duration accessTokenTtl,

        Duration refreshTokenTtl,

        Resource publicKeyLocation,

        Resource privateKeyLocation
) {
    public boolean hasExternalKeys() {
        return publicKeyLocation != null && privateKeyLocation != null;
    }
}
