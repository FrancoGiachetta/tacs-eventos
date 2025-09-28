package tacs.redis_utils;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static tacs.redis_utils.EstadoInicializacionRedis.NO_INICIALIZADO;

@Service
@RequiredArgsConstructor
public class FlagsInicializacionRedis {
    RedissonClient redisson;

    public EstadoInicializacionRedis getEstadoInicializacion(String flagInicializacion) {
        RBucket<EstadoInicializacionRedis> flagInicializacionCola = redisson.getBucket(flagInicializacion);
        return Optional.ofNullable(flagInicializacionCola.get()).orElse(NO_INICIALIZADO);
    }

    public void setEstadoInicializacion(String flagInicializacion, EstadoInicializacionRedis estado) {
        RBucket<EstadoInicializacionRedis> flagInicializacionCola = redisson.getBucket(flagInicializacion);
        flagInicializacionCola.set(estado);
    }
}
