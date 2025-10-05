package tacs.eventos.service.inscripciones;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tacs.eventos.model.Evento;
import tacs.eventos.model.Usuario;
import tacs.eventos.model.inscripcion.EstadoInscripcion;
import tacs.eventos.model.inscripcion.InscripcionEvento;
import tacs.eventos.model.inscripcion.InscripcionFactory;
import tacs.eventos.repository.inscripcion.InscripcionesRepository;
import tacs.eventos.service.WaitlistService;

import java.util.List;
import java.util.Optional;

/**
 * Punto de entrada para realizar o cancelar inscripciones, pasando por la waitlist si es necesario, y realizando las
 * validaciones pertientes.
 */
@Service
@AllArgsConstructor
public class InscripcionesService {
    private InscripcionesRepository inscripcionesRepository;
    private WaitlistService waitlistService;
    private CupoEventoService cupoEventoService;

    /**
     * Intenta inscribir al usuario al evento. Si no hay lugar, lo manda a la waitlist.
     *
     * @param evento
     * @param usuario
     *
     * @return la inscripción generada si pudo inscribirlo, o un Optional vacío si lo mandó a la waitlist.
     *
     * @throws EventoCerradoException
     *             si el evento está cerrado y ya no recibe inscripciones
     */
    public Optional<InscripcionEvento> inscribirOMandarAWaitlist(Evento evento, Usuario usuario)
            throws EventoCerradoException {
        if (!evento.isAbierto())
            throw new EventoCerradoException(evento);
        // Primero intenta inscribirlo directamente. Si no, lo manda a la waitlist.
        return intentarInscribir(InscripcionFactory.confirmada(usuario, evento)).or(() -> {
            InscripcionEvento inscripcionEvento = InscripcionFactory.pendiente(usuario, evento);
            inscripcionesRepository.save(inscripcionEvento);
            waitlistService.waitlist(evento).agregar(inscripcionEvento.getId());
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
        var optInscripcionACancelar = inscripcionNoCancelada(evento, usuario);
        if (optInscripcionACancelar.isEmpty())
            return; // Si ya está cancelada, no hago nada.
        var inscripcion = optInscripcionACancelar.get();
        var estabaConfirmada = inscripcion.estaConfirmada();
        inscripcion.cancelar();
        inscripcionesRepository.save(inscripcion);
        if (estabaConfirmada) { // Si se eliminó una inscripción confirmada (se liberó un lugar)
            /*
             * Promueve al próximo de la waitlist (si hay alguien). Hace esto en forma asincrónica, porque es una acción
             * que puede tardar (ya que la inscripción está sincronizada por evento), y al usuario que canceló su
             * inscripción le tenemos que devolver en el momento el response confirmandole que su incscipción fue
             * cancelada.
             */
            cupoEventoService.devolverCupo(evento);
            inscribirProximo(evento);
        }
    }

    /**
     * @param evento
     * @param usuario
     *
     * @return si el usuario está en la waitlist o tiene una inscripción confirmada para ese evento
     */
    public Optional<InscripcionEvento> inscripcionNoCancelada(Evento evento, Usuario usuario) {
        return inscripcionesRepository.noCanceladaParaParticipanteYEvento(usuario, evento);
    }

    /**
     * @param evento
     *
     * @return todas las inscripciones (confirmadas, canceladas, o pendientes) de ese evento
     */
    public List<InscripcionEvento> inscripcionesConfirmadas(Evento evento) {
        return inscripcionesRepository.findByEventoAndEstado(evento, EstadoInscripcion.CONFIRMADA);
    }

    /**
     * @param evento
     *
     * @return las inscripciones pendientes de ese evento
     */
    public List<InscripcionEvento> inscripcionesPendientes(Evento evento) {
        return inscripcionesRepository.findByEventoAndEstado(evento, EstadoInscripcion.PENDIENTE);
    }

    /**
     * Intenta inscribir al usuario directamente al evento (sin pasar por la waitlist).
     *
     * @param inscripcion
     *            la inscripción que se quiere intentar realizar
     *
     * @return la inscripción realizada, o un Optional vacío si no pudo realizar la inscripción porque no había lugar o
     *         porque el evento fue cerrado.
     */
    private Optional<InscripcionEvento> intentarInscribir(InscripcionEvento inscripcion) {
        boolean hayCupo = cupoEventoService.obtenerCupo(inscripcion.getEvento());
        if (!hayCupo)
            return Optional.empty();
        try {
            inscripcion.confirmar();
            /* Guarda una inscripción nueva, o la actualiza con el estado CONFIRMADA */
            inscripcionesRepository.save(inscripcion);
            return Optional.of(inscripcion);
        } catch (Exception e) {
            cupoEventoService.devolverCupo(inscripcion.getEvento());
            throw e;
        }
    }

    /**
     * Confirma la siguiente inscripción no cancelada en la waitlist. Este métod0 es asincrónico.
     *
     * @param evento
     */
    /*
     * TODO: hacer esto N veces, por si por algún error en el sistema habían quedado lugares libres en el evento sin
     * llenar
     */
    @Async
    protected void inscribirProximo(Evento evento) {
        waitlistService.waitlist(evento).proxima().ifPresent(this::intentarInscribir);
    }
}
