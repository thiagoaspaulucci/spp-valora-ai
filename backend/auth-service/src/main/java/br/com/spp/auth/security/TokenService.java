package br.com.spp.auth.security;

import br.com.spp.auth.config.JwtProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Emissão dos JWT de acesso e de renovação (refresh).
 *
 * <p>O refresh token carrega o claim {@code type=refresh} e um identificador
 * único ({@code jti}) usado pelo {@link RefreshTokenStore} para revogação e
 * rotação.</p>
 */
@Service
public class TokenService {

    private static final String CLAIM_ROLES = "roles";
    private static final String CLAIM_TYPE = "type";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    private final JwtEncoder jwtEncoder;
    private final JwtProperties properties;

    public TokenService(JwtEncoder jwtEncoder, JwtProperties properties) {
        this.jwtEncoder = jwtEncoder;
        this.properties = properties;
    }

    /** Refresh token emitido, com metadados necessários para o store. */
    public record RefreshToken(String value, String jti, Instant expiresAt) {
    }

    public String generateAccessToken(Authentication authentication) {
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        JwtClaimsSet claims = baseClaims(authentication.getName())
                .expiresAt(Instant.now().plus(properties.accessTokenTtl()))
                .claim(CLAIM_ROLES, roles)
                .claim(CLAIM_TYPE, TYPE_ACCESS)
                .build();

        return encode(claims);
    }

    public RefreshToken generateRefreshToken(Authentication authentication) {
        String jti = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plus(properties.refreshTokenTtl());

        JwtClaimsSet claims = baseClaims(authentication.getName())
                .id(jti)
                .expiresAt(expiresAt)
                .claim(CLAIM_TYPE, TYPE_REFRESH)
                .build();

        return new RefreshToken(encode(claims), jti, expiresAt);
    }

    public boolean isRefreshToken(Jwt jwt) {
        return TYPE_REFRESH.equals(jwt.getClaimAsString(CLAIM_TYPE));
    }

    private JwtClaimsSet.Builder baseClaims(String subject) {
        Instant now = Instant.now();
        return JwtClaimsSet.builder()
                .issuer(properties.issuer())
                .issuedAt(now)
                .subject(subject);
    }

    private String encode(JwtClaimsSet claims) {
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
