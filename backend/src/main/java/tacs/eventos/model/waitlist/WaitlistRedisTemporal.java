package tacs.eventos.model.waitlist;

import org.redisson.api.RedissonClient;
import tacs.eventos.model.Evento;
import tacs.eventos.model.inscripcion.EstadoInscripcion;
import tacs.eventos.model.inscripcion.InscripcionEvento;
import tacs.eventos.repository.inscripcion.InscripcionesRepository;

import java.util.Optional;

/**
 * Waitlist para ser usada cuando la Queue de Redis se está inicializando. Va a buscar la próxima inscripción
 * directamente a mongo, e insertar las nuevas inscripciones que se ingresen a la cola en una cola de Redis temporal.
 */
public class WaitlistRedisTemporal extends WaitlistRedis {
    private InscripcionesRepository inscripcionesRepository;

    public WaitlistRedisTemporal(Evento evento, RedissonClient redisson, String prefijoIdDeCola, InscripcionesRepository inscripcionesRepository) {
        super(evento, redisson, prefijoIdDeCola);
        this.inscripcionesRepository = inscripcionesRepository;
    }

    @Override
    public Optional<String> proximo() {
        return inscripcionesRepository.findFirstByEventoAndEstado(evento, EstadoInscripcion.PENDIENTE)
                .map(InscripcionEvento::getEvento).map(Evento::getId);
    }
}
