package tacs.eventos.service;

import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tacs.eventos.model.Evento;
import tacs.eventos.model.Usuario;
import tacs.eventos.model.Waitlist;
import tacs.eventos.model.inscripcion.InscripcionEvento;
import tacs.eventos.model.inscripcion.InscripcionFactory;
import tacs.eventos.repository.WaitlistRepository;
import tacs.eventos.repository.inscripcion.InscripcionesRepository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Punto de entrada para realizar o cancelar inscripciones, pasando por la
 * waitlist si es necesario, y realizando las
 * validaciones pertientes.
 */
@Service
@AllArgsConstructor
public class InscripcionesService {

    private InscripcionesRepository inscripcionesRepository;

    private WaitlistRepository waitlistRepository;

    private final InscripcionAsyncService inscripcionAsyncService;

    /**
     * Intenta inscribir al usuario al evento. Si no hay lugar, lo manda a la
     * waitlist.
     *
     * @param evento
     * @param usuario
     *
     * @return la inscripción generada si pudo inscribirlo, o un Optional vacío si
     *         lo mandó a la waitlist.
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public Optional<InscripcionEvento> inscribirOMandarAWaitlist(Evento evento, Usuario usuario)
            throws InterruptedException, ExecutionException {
        Optional<InscripcionEvento> inscripcion = inscripcionAsyncService
                .intentarInscribir(InscripcionFactory.directa(usuario, evento)).get();

        // Primero intenta inscribirlo directamente. Si no, lo manda a la waitlist.
        return inscripcion.or(() -> {
            Waitlist waitlist = waitlistRepository.waitlist(evento);
            waitlist.agregar(usuario);
            return Optional.empty();
        });
    }

    /**
     * Si el usuario estaba inscripto, cancela su inscripción. Si no estaba
     * inscripto, lo saca de la waitlist (si es que
     * estaba). Si se sacó de la waitlist, confirma la incripción del próximo, pero
     * en forma asincrónica, para no
     * demorar la respuesta de este métod0.
     *
     * @param evento
     * @param usuario
     */
    public void cancelarInscripcion(Evento evento, Usuario usuario) {
        // Si el usuario estaba inscripto, cancela su inscripción. Si no, intenta
        // sacarlo de la waitlist.
        var inscripcionConfirmada = inscripcionesRepository.getInscripcionConfirmada(usuario, evento);
        inscripcionConfirmada.ifPresentOrElse(InscripcionEvento::cancelar,
                () -> waitlistRepository.waitlist(evento).anularInscripcion(usuario));
        if (inscripcionConfirmada.isPresent()) { // Si se eliminó una inscripción confirmada (se liberó un lugar)
            // Promueve al próximo de la waitlist (si hay alguien). Hace esto en forma
            // asincrónica, porque es una acción
            // que
            // puede tardar (ya que la inscripción está sincronizada por evento), y al
            // usuario que canceló su
            // inscripción le
            // tenemos que devolver en el momento el response confirmandole que su
            // incscipción fue cancelada.
            inscripcionAsyncService.inscribirProximo(evento);
        }
    }

    /**
     * @param evento
     * @param usuario
     *
     * @return si el usuario está en la waitlist o tiene una inscripción confirmada
     *         para ese evento
     */
    public boolean inscripcionConfirmadaOEnWaitlist(Evento evento, Usuario usuario) {
        return inscripcionEstaConfirmada(evento, usuario) || inscripcionEstaEnWaitlist(evento, usuario);
    }

    /**
     * @param evento
     * @param usuario
     *
     * @return true si un usuario tiene una inscripción confirmada para el evento
     */
    public boolean inscripcionEstaConfirmada(Evento evento, Usuario usuario) {
        return buscarInscripcionConfirmada(usuario, evento).isPresent();
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

    public Optional<InscripcionEvento> buscarInscripcionConfirmada(Usuario usuario, Evento evento) {
        return inscripcionesRepository.getInscripcionConfirmada(usuario, evento);
    }

    public List<InscripcionEvento> buscarInscripcionesDeEvento(Evento evento) {
        return inscripcionesRepository.getInscripcionesPorEvento(evento);
    }

    public Waitlist buscarWaitlistDeEvento(Evento evento) {
        return waitlistRepository.waitlist(evento);
    }
}
