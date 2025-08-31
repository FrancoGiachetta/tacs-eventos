package tacs.eventos.service;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tacs.eventos.model.Evento;
import tacs.eventos.model.Usuario;
import tacs.eventos.model.Waitlist;
import tacs.eventos.model.inscripcion.InscripcionEvento;
import tacs.eventos.model.inscripcion.InscripcionFactory;
import tacs.eventos.repository.WaitlistRepository;
import tacs.eventos.repository.inscripcion.InscripcionesRepository;

import java.util.Optional;

/**
 * Punto de entrada para realizar o cancelar inscripciones, pasando por la waitlist si es necesario, y realizando las
 * validaciones pertientes.
 */
@Service
@AllArgsConstructor
public class InscripcionesService {

    private InscripcionesRepository inscripcionesRepository;

    private WaitlistRepository waitlistRepository;

    /**
     * Intenta inscribir al usuario al evento. Si no hay lugar, lo manda a la waitlist.
     *
     * @param evento
     * @param usuario
     *
     * @return la inscripción generada si pudo inscribirlo, o un Optional vacío si lo mandó a la waitlist.
     */
    public Optional<InscripcionEvento> inscribirOMandarAWaitlist(Evento evento, Usuario usuario) {
        // Primero intenta inscribirlo directamente. Si no, lo manda a la waitlist.
        return intentarInscribir(InscripcionFactory.directa(usuario, evento)).or(() -> {
            Waitlist waitlist = waitlistRepository.waitlist(evento);
            waitlist.agregar(usuario);
            return Optional.empty();
        });
    }

    /**
     * Si el usuario estaba inscripto, cancela su inscripción. Si no estaba inscripto, lo saca de la waitlist (si es que
     * estaba). Si no estaba inscripto y tampoco en la waitlist, no hace nada. Si se sacó de la waitlist, confirma la
     * incripción del próximo, pero en forma asincrónica, para no demorar la respuesta de este métod0.
     *
     * @param evento
     * @param usuario
     */
    public void cancelarInscripcion(Evento evento, Usuario usuario) {
        // Si el usuario estaba inscripto, cancela su inscripción
        inscripcionesRepository.getInscripcion(usuario, evento).ifPresentOrElse(InscripcionEvento::cancelar,
                () -> waitlistRepository.waitlist(evento).anularInscripcion(usuario));
        // Promueve al próximo de la waitlist (si hay alguien). Hace esto en forma asincrónica, porque es una acción que
        // puede tardar (ya que la inscripción está sincronizada por evento), y al usuario que canceló su inscripción le
        // tenemos que devolver en el momento el response confirmandole que su incscipción fue cancelada.
        inscribirProximo(evento);
    }

    /**
     * @param evento
     * @param usuario
     *
     * @return true si un usuario tiene una inscripción para el evento, y esta fue confirmada
     */
    public boolean inscripcionEstaConfirmada(Evento evento, Usuario usuario) {
        return inscripcionesRepository.getInscripcion(usuario, evento).isPresent();
    }

    /**
     * @param evento
     * @param usuario
     *
     * @return true si el usuario está en la waitlist del evento, false si no
     */
    public boolean inscripcionEstaEnWaitlist(Evento evento, Usuario usuario) {
        return waitlistRepository.waitlist(evento).contiene(usuario);
    }

    /**
     * Intenta inscribir al usuario directamente al evento (sin pasar por la waitlist).
     *
     * @param inscripcion
     *            la inscripción que se quiere intentar realizar
     *
     * @return la inscripción realizada, o un Optional vacío si no pudo realizar la inscripción porque no había lugar
     */
    private Optional<InscripcionEvento> intentarInscribir(InscripcionEvento inscripcion) {
        // Sincronizo la inscripción por evento. Esto es para evitar que entre el momento en el que se chequeó si había
        // lugar, y se persistió la inscripción en la base, el evento justo alcance la capacidad máxima. Si eso ocurre,
        // la cantidad de inscriptos en un evento puede superar el cupo máximo, y eso nunca puede pasar.
        // TODO: esta forma de lockear en realidad no está del todo OK porque lockea por objeto físico. Nada nos asegura
        // que no se instance el evento con el mismo id en otro hilo, y no se actualice al mismo tiempo. De todas
        // formas, capaz deberíamos usar otra estrategia de lockeo más liviana como usar un número de versión (como hace
        // hibernate) o el hash del estado interno del evento (como lo que vimos en la clase de API REST que se hace con
        // el ETag.
        synchronized (inscripcion.getEvento()) {
            int inscriptos = this.inscripcionesRepository.cantidadInscriptos(inscripcion.getEvento());
            if (!inscripcion.getEvento().permiteIncripcion(inscriptos))
                return Optional.empty();
            inscripcionesRepository.guardarInscripcion(inscripcion);
            return Optional.of(inscripcion);
        }
    }

    /**
     * Promueve al próximo de la waitlist a inscripción, si es que hay alguien en la waitlist. Este métod0 es
     * asincrónico.
     *
     * @param evento
     */
    @Async
    protected void inscribirProximo(Evento evento) {
        waitlistRepository.waitlist(evento).proximo()
                .map(inscripcionEnWaitlist -> InscripcionFactory.desdeWaitlist(evento, inscripcionEnWaitlist))
                .ifPresent(this::intentarInscribir);
    }
}
