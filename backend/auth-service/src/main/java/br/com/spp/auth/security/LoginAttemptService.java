package br.com.spp.auth.security;

import br.com.spp.auth.config.LoginProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Contabiliza tentativas de login falhas por e-mail (no Redis) e bloqueia
 * temporariamente após exceder o limite configurado, mitigando força bruta.
 */
@Service
public class LoginAttemptService {

    private static final String KEY_PREFIX = "login-attempts:";

    private final StringRedisTemplate redisTemplate;
    private final LoginProperties properties;

    public LoginAttemptService(StringRedisTemplate redisTemplate, LoginProperties properties) {
        this.redisTemplate = redisTemplate;
        this.properties = properties;
    }

    public boolean isBlocked(String email) {
        String value = redisTemplate.opsForValue().get(key(email));
        return value != null && Integer.parseInt(value) >= properties.maxAttempts();
    }

    public void recordFailure(String email) {
        String redisKey = key(email);
        Long attempts = redisTemplate.opsForValue().increment(redisKey);
        if (attempts != null && attempts == 1L) {
            redisTemplate.expire(redisKey, properties.blockDuration());
        }
    }

    public void reset(String email) {
        redisTemplate.delete(key(email));
    }

    private String key(String email) {
        return KEY_PREFIX + email;
    }
}
