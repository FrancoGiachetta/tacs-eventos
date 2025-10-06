package tacs.eventos.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@TestConfiguration
public class TestSessionConfiguration {

    static {
        System.setProperty("spring.session.store-type", "none");
        System.setProperty("testcontainers.reuse.enable", "true");
        System.setProperty("testcontainers.ryuk.disabled", "true");
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // Proporciona una fábrica de conexiones simulada
        return new LettuceConnectionFactory();
    }
}
