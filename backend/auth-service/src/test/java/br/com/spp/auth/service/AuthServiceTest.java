package br.com.spp.auth.service;

import br.com.spp.auth.config.JwtProperties;
import br.com.spp.auth.dto.LoginRequest;
import br.com.spp.auth.dto.RefreshRequest;
import br.com.spp.auth.dto.RegisterRequest;
import br.com.spp.auth.dto.TokenResponse;
import br.com.spp.auth.entity.User;
import br.com.spp.auth.exception.EmailAlreadyExistsException;
import br.com.spp.auth.exception.InvalidTokenException;
import br.com.spp.auth.exception.TooManyLoginAttemptsException;
import br.com.spp.auth.mapper.UserMapper;
import br.com.spp.auth.repository.UserRepository;
import br.com.spp.auth.security.LoginAttemptService;
import br.com.spp.auth.security.RefreshTokenStore;
import br.com.spp.auth.security.TokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserDetailsService userDetailsService;
    @Mock private TokenService tokenService;
    @Mock private RefreshTokenStore refreshTokenStore;
    @Mock private LoginAttemptService loginAttemptService;
    @Mock private JwtDecoder jwtDecoder;
    @Mock private UserMapper userMapper;

    private final JwtProperties jwtProperties =
            new JwtProperties("issuer", Duration.ofMinutes(15), Duration.ofDays(7), null, null);

    private AuthService authService() {
        return new AuthService(userRepository, passwordEncoder, authenticationManager,
                userDetailsService, tokenService, refreshTokenStore, loginAttemptService,
                jwtDecoder, jwtProperties, userMapper);
    }

    private void stubTokenIssuance(Authentication authentication) {
        when(tokenService.generateAccessToken(authentication)).thenReturn("access");
        when(tokenService.generateRefreshToken(authentication)).thenReturn(
                new TokenService.RefreshToken("refresh", "jti-1", Instant.now().plusSeconds(3600)));
    }

    @Test
    void registerShouldPersistUserAndReturnTokens() {
        when(userRepository.existsByEmail("maria@exemplo.com")).thenReturn(false);
        when(passwordEncoder.encode("senhaForte123")).thenReturn("hash");
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        stubTokenIssuance(authentication);

        TokenResponse response = authService()
                .register(new RegisterRequest("Maria", "maria@exemplo.com", "senhaForte123"));

        assertThat(response.accessToken()).isEqualTo("access");
        assertThat(response.refreshToken()).isEqualTo("refresh");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(900L);
        verify(userRepository).save(any(User.class));
        verify(refreshTokenStore).store(eq("jti-1"), any(), any(Duration.class));
    }

    @Test
    void registerShouldFailWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail("maria@exemplo.com")).thenReturn(true);

        assertThatThrownBy(() -> authService()
                .register(new RegisterRequest("Maria", "maria@exemplo.com", "senhaForte123")))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void loginShouldReturnTokensAndResetAttempts() {
        when(loginAttemptService.isBlocked("maria@exemplo.com")).thenReturn(false);
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        stubTokenIssuance(authentication);

        TokenResponse response = authService()
                .login(new LoginRequest("maria@exemplo.com", "senhaForte123"));

        assertThat(response.accessToken()).isEqualTo("access");
        verify(loginAttemptService).reset("maria@exemplo.com");
    }

    @Test
    void loginShouldRecordFailureAndPropagateBadCredentials() {
        when(loginAttemptService.isBlocked("maria@exemplo.com")).thenReturn(false);
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("bad"));

        assertThatThrownBy(() -> authService()
                .login(new LoginRequest("maria@exemplo.com", "errada")))
                .isInstanceOf(BadCredentialsException.class);

        verify(loginAttemptService).recordFailure("maria@exemplo.com");
    }

    @Test
    void loginShouldBlockAfterTooManyAttempts() {
        when(loginAttemptService.isBlocked("maria@exemplo.com")).thenReturn(true);

        assertThatThrownBy(() -> authService()
                .login(new LoginRequest("maria@exemplo.com", "qualquer")))
                .isInstanceOf(TooManyLoginAttemptsException.class);

        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void refreshShouldRejectAccessTokenType() {
        Jwt jwt = mock(Jwt.class);
        when(jwtDecoder.decode("token")).thenReturn(jwt);
        when(tokenService.isRefreshToken(jwt)).thenReturn(false);

        assertThatThrownBy(() -> authService().refresh(new RefreshRequest("token")))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void refreshShouldRejectRevokedToken() {
        Jwt jwt = mock(Jwt.class);
        when(jwtDecoder.decode("token")).thenReturn(jwt);
        when(tokenService.isRefreshToken(jwt)).thenReturn(true);
        when(jwt.getId()).thenReturn("jti-x");
        when(refreshTokenStore.isActive("jti-x")).thenReturn(false);

        assertThatThrownBy(() -> authService().refresh(new RefreshRequest("token")))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void refreshShouldRejectMalformedToken() {
        when(jwtDecoder.decode("bad")).thenThrow(new BadJwtException("invalid"));

        assertThatThrownBy(() -> authService().refresh(new RefreshRequest("bad")))
                .isInstanceOf(InvalidTokenException.class);
    }
}
