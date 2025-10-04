package tacs.eventos.service;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;
import tacs.eventos.model.Evento;
import tacs.eventos.model.Usuario;
import tacs.eventos.model.inscripcion.InscripcionEvento;
import tacs.eventos.model.inscripcion.InscripcionFactory;
import tacs.eventos.repository.WaitlistRepository;
import tacs.eventos.repository.inscripcion.InscripcionesRepository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Punto de entrada para realizar o cancelar inscripciones, pasando por la waitlist si es necesario, y realizando las
 * validaciones pertientes.
 */
@Service
@AllArgsConstructor
public class InscripcionesService {

    private InscripcionesRepository inscripcionesRepository;

    private WaitlistRepository waitlistRepository;

    private final InscripcionAsyncService inscripcionAsyncService;

    /**
     * Intenta inscribir al usuario al evento. Si no hay lugar, lo manda a la waitlist.
     *
     * @param evento
     * @param usuario
     *
     * @return la inscripción generada si pudo inscribirlo, o un Optional vacío si lo mandó a la waitlist.
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public Optional<InscripcionEvento> inscribirOMandarAWaitlist(Evento evento, Usuario usuario)
            throws InterruptedException, ExecutionException {
        Optional<InscripcionEvento> inscripcion = inscripcionAsyncService
                .intentarInscribir(InscripcionFactory.confirmada(usuario, evento)).get();

        // Primero intenta inscribirlo directamente. Si no, lo manda a la waitlist.
        return inscripcion.or(() -> {
            InscripcionEvento inscripcionEvento = InscripcionFactory.pendiente(usuario, evento);
            inscripcionesRepository.guardarInscripcion(inscripcionEvento);
            waitlistRepository.waitlist(evento).agregar(inscripcionEvento.getId());
            return Optional.empty();
        });
    }

    /**
     * Si el usuario estaba inscripto, cancela su inscripción. Si no estaba inscripto, lo saca de la waitlist (si es que
     * estaba). Si se sacó de la waitlist, confirma la incripción del próximo, pero en forma asincrónica, para no
     * demorar la respuesta de este métod0.
     *
     * @param evento
     * @param usuario
     */
    public void cancelarInscripcion(Evento evento, Usuario usuario) {
        // Si el usuario estaba inscripto, cancela su inscripción. Si no, intenta sacarlo de la waitlist.
        var inscripcion = inscripcionesRepository.getInscripcionParaUsuarioYEvento(usuario, evento);
        var estabaConfirmada = inscripcion.map(InscripcionEvento::estaConfirmada).orElse(false);
        inscripcion.ifPresent(InscripcionEvento::cancelar);
        if (estabaConfirmada) { // Si se eliminó una inscripción confirmada (se liberó un lugar)
            // Promueve al próximo de la waitlist (si hay alguien). Hace esto en forma asincrónica, porque es una acción
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
     * @return si el usuario está en la waitlist o tiene una inscripción confirmada para ese evento
     */
    public boolean inscripcionConfirmadaOEnWaitlist(Evento evento, Usuario usuario) {
        return inscripcionesRepository.getInscripcionParaUsuarioYEvento(usuario, evento)
                .map(i -> i.estaConfirmada() || i.estaPendiente()).orElse(false);
    }

    /**
     * @param evento
     *
     * @return todas las inscripciones (confirmadas, canceladas, o pendientes) de ese evento
     */
    public List<InscripcionEvento> buscarInscripcionesDeEvento(Evento evento) {
        return inscripcionesRepository.getInscripcionesPorEvento(evento);
    }

    /**
     * @param evento
     *
     * @return las inscripciones pendientes de ese evento
     */
    public List<InscripcionEvento> inscripcionesPendientes(Evento evento) {
        return inscripcionesRepository.getInscripcionesPendientes(evento);
    }

    public Optional<InscripcionEvento> inscripcionParaUsuarioYEvento(Usuario usuarioInscripto, Evento evento) {
        return inscripcionesRepository.getInscripcionParaUsuarioYEvento(usuarioInscripto, evento);
    }
}
