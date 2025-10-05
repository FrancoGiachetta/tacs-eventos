package tacs.eventos.config;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import redis.embedded.RedisServer;
import org.springframework.data.redis.core.StringRedisTemplate;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;

@TestConfiguration
public class TestRedisConfiguration {

    private RedisServer redisServer;
    private final int redisPort = 6370; // Puerto diferente para evitar conflictos

    public TestRedisConfiguration() {
        this.redisServer = new RedisServer(redisPort);
    }
    @Bean
    @Primary
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return new StringRedisTemplate(redisConnectionFactory);
    }
    @PostConstruct
    public void postConstruct() throws IOException {
        try {
            redisServer.start();
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("Address already in use")) {
                // Ya está en ejecución
            } else {
                throw e;
            }
        }
    }

    @PreDestroy
    public void preDestroy() {
        if (redisServer != null && redisServer.isActive()) {
            redisServer.stop();
        }
    }

    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration("localhost", redisPort);
        return new LettuceConnectionFactory(config);
    }

    @Bean
    @Primary
    public RedissonClient redisson() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://localhost:" + redisPort);
        return Redisson.create(config);
    }
}