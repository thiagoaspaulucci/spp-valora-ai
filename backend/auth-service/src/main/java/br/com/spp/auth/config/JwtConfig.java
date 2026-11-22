package br.com.spp.auth.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.oauth2.jwt.*;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

/**
 * Infraestrutura de assinatura/validação dos JWT (RSA-256).
 *
 * <p>Em produção as chaves são carregadas de recursos externos
 * ({@code spp.security.jwt.*-key-location}), o que permite reinício sem
 * invalidar tokens e escala horizontal (todas as instâncias compartilham a
 * mesma chave). Sem configuração, gera-se um par efêmero — apenas dev.</p>
 */
@Configuration
public class JwtConfig {

    private static final Logger log = LoggerFactory.getLogger(JwtConfig.class);

    @Bean
    public RSAKey rsaKey(JwtProperties properties) {
        if (properties.hasExternalKeys()) {
            return loadExternalKey(properties);
        }
        log.warn("Nenhuma chave RSA externa configurada. Gerando par EFÊMERO — "
                + "adequado apenas para desenvolvimento. Configure "
                + "spp.security.jwt.public-key-location/private-key-location em produção.");
        return generateEphemeralKey();
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource(RSAKey rsaKey) {
        return new ImmutableJWKSet<>(new JWKSet(rsaKey));
    }

    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public JwtDecoder jwtDecoder(RSAKey rsaKey) {
        try {
            return NimbusJwtDecoder.withPublicKey(rsaKey.toRSAPublicKey()).build();
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao construir o JwtDecoder", e);
        }
    }

    private RSAKey loadExternalKey(JwtProperties properties) {
        try {
            RSAPublicKey publicKey = RsaKeyConverters.x509()
                    .convert(properties.publicKeyLocation().getInputStream());
            RSAPrivateKey privateKey = RsaKeyConverters.pkcs8()
                    .convert(properties.privateKeyLocation().getInputStream());
            log.info("Chave RSA carregada de recurso externo.");
            return new RSAKey.Builder(publicKey)
                    .privateKey(privateKey)
                    .keyID("spp-auth-key")
                    .build();
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao ler as chaves RSA externas", e);
        }
    }

    private RSAKey generateEphemeralKey() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair keyPair = generator.generateKeyPair();
            return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                    .privateKey((RSAPrivateKey) keyPair.getPrivate())
                    .keyID(UUID.randomUUID().toString())
                    .build();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Falha ao gerar par de chaves RSA", e);
        }
    }
}
