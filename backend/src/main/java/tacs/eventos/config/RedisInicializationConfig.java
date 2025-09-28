package tacs.eventos.config;

import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tacs.redis_utils.RedisConnectionListener;

@Configuration
@RequiredArgsConstructor
public class RedisInicializationConfig {
    private RedissonClient redissonClient; // el bean ya creado con properties
    private RedisConnectionListener redisConnectionListener;

    @Bean
    public RedissonClient redissonClientWithListener() {
        /* A la configuración especificada en el properties, le agrega esto */
        return Redisson.create(redissonClient.getConfig().setConnectionListener(redisConnectionListener));
    }
}
