package tacs.eventos.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.redisson.spring.starter.RedissonAutoConfigurationV2;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import jakarta.annotation.PostConstruct;

@TestConfiguration
@AutoConfigureBefore({ RedissonAutoConfigurationV2.class, RedisAutoConfiguration.class })
public class TestRedisConfiguration {

    private static final GenericContainer<?> REDIS_CONTAINER = new GenericContainer<>(
            DockerImageName.parse("redis:7-alpine")).withExposedPorts(6379);

    static {
        REDIS_CONTAINER.start();
        // Configurar propiedades de Spring para Redis
        System.setProperty("spring.data.redis.host", REDIS_CONTAINER.getHost());
        System.setProperty("spring.data.redis.port", REDIS_CONTAINER.getFirstMappedPort().toString());
        // La antigua propiedad también para compatibilidad
        System.setProperty("spring.redis.host", REDIS_CONTAINER.getHost());
        System.setProperty("spring.redis.port", REDIS_CONTAINER.getFirstMappedPort().toString());
        System.setProperty("spring.session.store-type", "none");
    }

    @Bean
    @Primary
    public RedissonClient redisson() {
        Config config = new Config();
        String redisAddress = String.format("redis://%s:%d", REDIS_CONTAINER.getHost(),
                REDIS_CONTAINER.getFirstMappedPort());
        config.useSingleServer().setAddress(redisAddress).setConnectionMinimumIdleSize(1).setConnectionPoolSize(2);
        return Redisson.create(config);
    }

    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactory(RedissonClient redisson) {
        return new RedissonConnectionFactory(redisson);
    }

    @Bean
    @Primary
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return new StringRedisTemplate(redisConnectionFactory);
    }

    @Bean
    @Primary
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println("Redis test container iniciado en: " + REDIS_CONTAINER.getHost() + ":"
                + REDIS_CONTAINER.getFirstMappedPort());
    }
}
