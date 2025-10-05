package tacs.eventos.service.waitlist.redis;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import tacs.eventos.model.Evento;
import tacs.eventos.model.waitlist.Waitlist;
import tacs.eventos.model.waitlist.WaitlistMongo;
import tacs.eventos.model.waitlist.WaitlistRedis;
import tacs.eventos.repository.inscripcion.InscripcionesRepository;
import tacs.eventos.service.WaitlistService;

import static tacs.eventos.redis_utils.EstadoInicializacionRedis.LISTO;
import static tacs.eventos.redis_utils.EstadoInicializacionRedis.NO_INICIALIZADO;

@Service
@RequiredArgsConstructor
public class RedisWaitlistService implements WaitlistService {
    private final InscripcionesRepository inscripcionesRepository;
    private final RedissonClient redisson;
    private final InicializacionWaitlistRedisService inicializacion;

    @Override
    public Waitlist waitlist(Evento evento) {
        if (inicializacion.estadoInicializacionWaitlist(evento) == NO_INICIALIZADO)
            inicializacion.inicializarWaitlist(evento, watilistPermanente(evento));

        return inicializacion.estadoInicializacionWaitlist(evento) == LISTO ? watilistPermanente(evento)
                : waitlistTemporal(evento);
    }

    private @NonNull WaitlistRedis watilistPermanente(Evento evento) {
        return new WaitlistRedis(evento, redisson, "evento:waitlist:", inscripcionesRepository);
    }

    private @NonNull WaitlistMongo waitlistTemporal(Evento evento) {
        return new WaitlistMongo(evento, inscripcionesRepository);
    }
}
