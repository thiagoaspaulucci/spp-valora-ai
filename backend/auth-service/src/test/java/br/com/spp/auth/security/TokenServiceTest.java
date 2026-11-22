package br.com.spp.auth.security;

import br.com.spp.auth.config.JwtProperties;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.*;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TokenServiceTest {

    private TokenService tokenService;
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();

        RSAKey rsaKey = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey((RSAPrivateKey) keyPair.getPrivate())
                .keyID("test-key")
                .build();
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(rsaKey));

        JwtEncoder encoder = new NimbusJwtEncoder(jwkSource);
        this.jwtDecoder = NimbusJwtDecoder.withPublicKey((RSAPublicKey) keyPair.getPublic()).build();

        JwtProperties properties = new JwtProperties("test-issuer",
                Duration.ofMinutes(15), Duration.ofDays(7), null, null);
        this.tokenService = new TokenService(encoder, properties);
    }

    @Test
    void accessTokenShouldContainSubjectRolesAndType() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "maria@exemplo.com", null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        Jwt jwt = jwtDecoder.decode(tokenService.generateAccessToken(authentication));

        assertThat(jwt.getClaimAsString("iss")).isEqualTo("test-issuer");
        assertThat(jwt.getSubject()).isEqualTo("maria@exemplo.com");
        assertThat(jwt.getClaimAsStringList("roles")).containsExactly("ROLE_USER");
        assertThat(jwt.getClaimAsString("type")).isEqualTo("access");
        assertThat(tokenService.isRefreshToken(jwt)).isFalse();
    }

    @Test
    void refreshTokenShouldBeIdentifiedAsRefreshAndCarryJti() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "maria@exemplo.com", null, List.of());

        TokenService.RefreshToken refreshToken = tokenService.generateRefreshToken(authentication);
        Jwt jwt = jwtDecoder.decode(refreshToken.value());

        assertThat(tokenService.isRefreshToken(jwt)).isTrue();
        assertThat(refreshToken.jti()).isNotBlank();
        assertThat(jwt.getId()).isEqualTo(refreshToken.jti());
    }
}
