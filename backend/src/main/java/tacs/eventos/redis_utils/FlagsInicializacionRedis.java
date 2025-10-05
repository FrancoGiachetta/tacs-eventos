package tacs.eventos.redis_utils;

import lombok.AllArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static tacs.eventos.redis_utils.EstadoInicializacionRedis.NO_INICIALIZADO;

@Service
@AllArgsConstructor
public class FlagsInicializacionRedis {
    private final RedissonClient redisson;

    public EstadoInicializacionRedis getEstadoInicializacion(String flagInicializacion) {
        RBucket<String> flagInicializacionCola = redisson.getBucket(flagInicializacion, new JsonJacksonCodec());
        return Optional.ofNullable(flagInicializacionCola.get()).map(EstadoInicializacionRedis::valueOf)
                .orElse(NO_INICIALIZADO);
    }

    public void setEstadoInicializacion(String flagInicializacion, EstadoInicializacionRedis estado) {
        RBucket<String> flagInicializacionCola = redisson.getBucket(flagInicializacion, new JsonJacksonCodec());
        flagInicializacionCola.set(estado.name());
    }
}
