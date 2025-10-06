package tacs.eventos.repository.sesion;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import tacs.eventos.model.Session;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
public class SessionRedisRepository implements SessionRepository {
    @Value("${server.servlet.session.timeout}")
    private String sessionTimeout;
    private final StringRedisTemplate redisTemplate;
    private static final String PREFIX = "evento:token:";

    public SessionRedisRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Session save(Session session) {
        String key = PREFIX + session.getToken();
        redisTemplate.opsForValue().set(key, session.getUserId());
        redisTemplate.opsForValue().set(key, session.getUserId(), getSessionTimeoutInMinutes(), TimeUnit.MINUTES);

        return session;
    }

    @Override
    public Optional<Session> findByToken(String token) {
        String userId = redisTemplate.opsForValue().get(PREFIX + token);
        return userId != null ? Optional
                .of(new Session(token, userId, Instant.now().plus(Duration.ofMinutes(getSessionTimeoutInMinutes()))))
                : Optional.empty();
    }

    @Override
    public void invalidate(String token) {
        redisTemplate.delete(PREFIX + token);
    }

    private long getSessionTimeoutInMinutes() {
        if (sessionTimeout.endsWith("m")) {
            return Long.parseLong(sessionTimeout.replace("m", ""));
        } else if (sessionTimeout.endsWith("h")) {
            return Long.parseLong(sessionTimeout.replace("h", "")) * 60;
        } else if (sessionTimeout.endsWith("s")) {
            return Long.parseLong(sessionTimeout.replace("s", "")) / 60;
        }
        // Por defecto, intenta parsear como minutos
        return Long.parseLong(sessionTimeout);
    }
}
