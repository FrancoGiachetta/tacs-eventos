package tacs.eventos.model.waitlist;

import org.redisson.api.RedissonClient;
import tacs.eventos.model.Evento;
import tacs.eventos.model.inscripcion.InscripcionEvento;
import tacs.eventos.repository.inscripcion.InscripcionesRepository;

import java.util.Optional;
import java.util.Queue;

/**
 * Waitlist que guarda la cola como una Queue de Redis.
 */
public class WaitlistRedis implements Waitlist {
    protected final RedissonClient redisson;
    protected final Evento evento;
    protected final Queue<String> items;
    protected final InscripcionesRepository inscripcionesRepository;

    public WaitlistRedis(Evento evento, RedissonClient redisson, String prefijoIdDeCola,
            InscripcionesRepository inscripcionesRepository) {
        this.redisson = redisson;
        this.evento = evento;
        this.inscripcionesRepository = inscripcionesRepository;
        this.items = redisson.getQueue(prefijoIdDeCola + evento.getId());
    }

    public void agregar(String idInscripcion) {
        items.add(idInscripcion);
    }

    public Optional<InscripcionEvento> proxima() {
        String idInscripcion;
        while ((idInscripcion = items.poll()) != null) { // Va sacando de la cola
            var inscripcion = inscripcionesRepository.findById(idInscripcion);
            // Si la inscripción existe y está pendiente, la retorna. Si no, revisa la próxima
            if (inscripcion.isPresent() && inscripcion.get().estaPendiente())
                return inscripcion;
        }
        return Optional.empty(); // Cuando se haya agotado la cola, retorna Optional.empty()
    }
}
