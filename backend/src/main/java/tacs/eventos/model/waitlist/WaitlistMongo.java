package tacs.eventos.model.waitlist;

import lombok.AllArgsConstructor;
import tacs.eventos.model.evento.Evento;
import tacs.eventos.model.inscripcion.EstadoInscripcion;
import tacs.eventos.model.inscripcion.InscripcionEvento;
import tacs.eventos.repository.inscripcion.InscripcionesRepository;

import java.util.Optional;

/**
 * Waitlist para ser usada cuando la Queue de Redis se está inicializando. Va a buscar la próxima inscripción
 * directamente a mongo, y no hace nada en el métod0 agregar, porque las inscripciones pendientes siempre se persisten
 * en mongo independientemente de lo que haga la Waitlist.
 */
@AllArgsConstructor
public class WaitlistMongo implements Waitlist {
    private final Evento evento;
    private final InscripcionesRepository inscripcionesRepository;

    public void agregar(String idInscripcion) {
        // No agrega a ningun lado. Deja que se guarde solamente en la base de datos.
    }

    public Optional<InscripcionEvento> proxima() {
        return inscripcionesRepository.findFirstByEventoAndEstado(evento, EstadoInscripcion.PENDIENTE);
    }
}
