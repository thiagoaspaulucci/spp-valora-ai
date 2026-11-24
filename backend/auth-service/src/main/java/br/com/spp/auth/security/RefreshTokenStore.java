package br.com.spp.auth.security;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Registro de refresh tokens ativos no Redis, indexados pelo {@code jti}.
 * Permite revogação (logout) e rotação real (o jti antigo é removido ao
 * renovar, invalidando o refresh token anterior).
 */
@Component
public class RefreshTokenStore {

    private static final String KEY_PREFIX = "refresh:";

    private final StringRedisTemplate redisTemplate;

    public RefreshTokenStore(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void store(String jti, String subject, Duration ttl) {
        redisTemplate.opsForValue().set(KEY_PREFIX + jti, subject, ttl);
    }

    public boolean isActive(String jti) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(KEY_PREFIX + jti));
    }

    public void revoke(String jti) {
        redisTemplate.delete(KEY_PREFIX + jti);
    }
}
