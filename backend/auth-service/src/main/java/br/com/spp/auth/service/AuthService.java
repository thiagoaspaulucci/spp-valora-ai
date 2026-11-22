package br.com.spp.auth.service;

import br.com.spp.auth.config.JwtProperties;
import br.com.spp.auth.dto.*;
import br.com.spp.auth.entity.Role;
import br.com.spp.auth.entity.User;
import br.com.spp.auth.exception.EmailAlreadyExistsException;
import br.com.spp.auth.exception.InvalidTokenException;
import br.com.spp.auth.exception.TooManyLoginAttemptsException;
import br.com.spp.auth.mapper.UserMapper;
import br.com.spp.auth.repository.UserRepository;
import br.com.spp.auth.security.LoginAttemptService;
import br.com.spp.auth.security.RefreshTokenStore;
import br.com.spp.auth.security.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * Regras de negócio de autenticação: registro, login, renovação e logout.
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final org.springframework.security.authentication.AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final TokenService tokenService;
    private final RefreshTokenStore refreshTokenStore;
    private final LoginAttemptService loginAttemptService;
    private final JwtDecoder jwtDecoder;
    private final JwtProperties jwtProperties;
    private final UserMapper userMapper;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       org.springframework.security.authentication.AuthenticationManager authenticationManager,
                       UserDetailsService userDetailsService,
                       TokenService tokenService,
                       RefreshTokenStore refreshTokenStore,
                       LoginAttemptService loginAttemptService,
                       JwtDecoder jwtDecoder,
                       JwtProperties jwtProperties,
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.tokenService = tokenService;
        this.refreshTokenStore = refreshTokenStore;
        this.loginAttemptService = loginAttemptService;
        this.jwtDecoder = jwtDecoder;
        this.jwtProperties = jwtProperties;
        this.userMapper = userMapper;
    }

    @Transactional
    public TokenResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException();
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .enabled(true)
                .roles(new HashSet<>(Set.of(Role.ROLE_USER)))
                .build();

        userRepository.save(user);
        log.info("Novo usuário registrado: {}", user.getEmail());

        return authenticateAndIssue(request.email(), request.password());
    }

    public TokenResponse login(LoginRequest request) {
        if (loginAttemptService.isBlocked(request.email())) {
            throw new TooManyLoginAttemptsException();
        }
        try {
            TokenResponse tokens = authenticateAndIssue(request.email(), request.password());
            loginAttemptService.reset(request.email());
            return tokens;
        } catch (AuthenticationException ex) {
            loginAttemptService.recordFailure(request.email());
            throw ex;
        }
    }

    public TokenResponse refresh(RefreshRequest request) {
        Jwt jwt = decode(request.refreshToken());

        if (!tokenService.isRefreshToken(jwt) || jwt.getId() == null) {
            throw new InvalidTokenException("O token informado não é um refresh token");
        }
        if (!refreshTokenStore.isActive(jwt.getId())) {
            throw new InvalidTokenException("Refresh token revogado ou expirado");
        }

        // Rotação: o refresh atual é invalidado e um novo par é emitido.
        refreshTokenStore.revoke(jwt.getId());

        UserDetails userDetails = userDetailsService.loadUserByUsername(jwt.getSubject());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        return issueTokens(authentication);
    }

    public void logout(String refreshToken) {
        try {
            Jwt jwt = jwtDecoder.decode(refreshToken);
            if (tokenService.isRefreshToken(jwt) && jwt.getId() != null) {
                refreshTokenStore.revoke(jwt.getId());
            }
        } catch (JwtException ignored) {
            // Logout é idempotente: token inválido não gera erro.
        }
    }

    @Transactional(readOnly = true)
    public UserResponse currentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidTokenException("Usuário do token não encontrado"));
        return userMapper.toResponse(user);
    }

    private Jwt decode(String token) {
        try {
            return jwtDecoder.decode(token);
        } catch (JwtException e) {
            throw new InvalidTokenException("Refresh token inválido ou expirado");
        }
    }

    private TokenResponse authenticateAndIssue(String email, String rawPassword) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, rawPassword));
        return issueTokens(authentication);
    }

    private TokenResponse issueTokens(Authentication authentication) {
        String accessToken = tokenService.generateAccessToken(authentication);
        TokenService.RefreshToken refreshToken = tokenService.generateRefreshToken(authentication);

        Duration ttl = Duration.between(Instant.now(), refreshToken.expiresAt());
        refreshTokenStore.store(refreshToken.jti(), authentication.getName(), ttl);

        return new TokenResponse(accessToken, refreshToken.value(), "Bearer",
                jwtProperties.accessTokenTtl().toSeconds());
    }
}
