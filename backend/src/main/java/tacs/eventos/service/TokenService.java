package tacs.eventos.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenService {
    private final StringRedisTemplate redisTemplate;

    public TokenService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void guardarToken(String token, String userId) {
        redisTemplate.opsForValue().set(token, userId);
        // Si quieres que nunca expire, no pongas tiempo. Si quieres controlar expiración:
        // redisTemplate.opsForValue().set(token, userId, 7, TimeUnit.DAYS);
    }

    public boolean existeToken(String token) {
        return redisTemplate.hasKey(token);
    }

    public void eliminarToken(String token) {
        redisTemplate.delete(token);
    }
}
