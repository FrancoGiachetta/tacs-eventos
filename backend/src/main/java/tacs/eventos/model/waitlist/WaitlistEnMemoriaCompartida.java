package tacs.eventos.model.waitlist;

import lombok.RequiredArgsConstructor;
import tacs.eventos.model.Evento;
import tacs.eventos.model.inscripcion.InscripcionEvento;
import tacs.eventos.repository.inscripcion.InscripcionesRepository;

import java.util.Optional;
import java.util.Queue;

/**
 * Waitlist que guarda la cola como una Queue en una memoria compartida entre instancias del servicio, como por ejemplo
 * Redis.
 */
@RequiredArgsConstructor
public class WaitlistEnMemoriaCompartida implements Waitlist {
    protected final Evento evento;
    protected final Queue<String> items;
    protected final InscripcionesRepository inscripcionesRepository;

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
