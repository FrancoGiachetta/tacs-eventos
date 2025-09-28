package tacs.eventos.service;

import lombok.AllArgsConstructor;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import tacs.eventos.model.Evento;
import tacs.eventos.model.inscripcion.InscripcionEvento;
import tacs.eventos.model.inscripcion.InscripcionFactory;
import tacs.eventos.repository.WaitlistRepository;
import tacs.eventos.repository.inscripcion.InscripcionesRepository;

@Service
public class InscripcionAsyncService {
    private final InscripcionesRepository inscripcionesRepository;
    private final WaitlistRepository waitlistRepository;

    public InscripcionAsyncService(
            @Qualifier("inscripcionesInMemoryRepo") InscripcionesRepository inscripcionesRepository,
            @Qualifier("waitlistsInMemoryRepo") WaitlistRepository waitlistRepository) {
        this.inscripcionesRepository = inscripcionesRepository;
        this.waitlistRepository = waitlistRepository;
    }

    /**
     * Promueve al próximo de la waitlist a inscripción, si es que hay alguien en la waitlist. Este métod0 es
     * asincrónico.
     *
     * @param evento
     */
    @Async
    public void inscribirProximo(Evento evento) {
        waitlistRepository.waitlist(evento).proximo()
                .map(inscripcionEnWaitlist -> InscripcionFactory.desdeWaitlist(evento, inscripcionEnWaitlist))
                .ifPresent(this::intentarInscribir);
    }

    /**
     * Intenta inscribir al usuario directamente al evento (sin pasar por la waitlist).
     *
     * @param inscripcion
     *            la inscripción que se quiere intentar realizar
     *
     * @return la inscripción realizada, o un Optional vacío si no pudo realizar la inscripción porque no había lugar
     */
    @Async
    public Future<Optional<InscripcionEvento>> intentarInscribir(InscripcionEvento inscripcion) {
        // Sincronizo la inscripción por evento. Esto es para evitar que entre el
        // momento en el que se chequeó si había
        // lugar, y se persistió la inscripción en la base, el evento justo alcance la
        // capacidad máxima. Si eso ocurre,
        // la cantidad de inscriptos en un evento puede superar el cupo máximo, y eso
        // nunca puede pasar.
        // TODO: esta forma de lockear en realidad no está del todo OK porque lockea por
        // objeto físico. Nada nos asegura
        // que no se instance el evento con el mismo id en otro hilo, y no se actualice
        // al mismo tiempo. De todas
        // formas, capaz deberíamos usar otra estrategia de lockeo más liviana como usar
        // un número de versión (como hace
        // hibernate) o el hash del estado interno del evento (como lo que vimos en la
        // clase de API REST que se hace con
        // el ETag.
        synchronized (inscripcion.getEvento()) {
            int inscriptos = this.inscripcionesRepository.cantidadInscriptos(inscripcion.getEvento());
            if (!inscripcion.getEvento().permiteIncripcion(inscriptos))
                return CompletableFuture.completedFuture(Optional.empty());
            inscripcionesRepository.guardarInscripcion(inscripcion);
            return CompletableFuture.completedFuture(Optional.of(inscripcion));
        }
    }
}
