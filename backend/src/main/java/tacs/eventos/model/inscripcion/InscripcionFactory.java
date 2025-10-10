package tacs.eventos.model.inscripcion;

import tacs.eventos.model.evento.Evento;
import tacs.eventos.model.Usuario;

import java.time.LocalDateTime;

/**
 * Clase usada para crear instancias de InscripcionEvento.
 */
public class InscripcionFactory {

    /**
     * Crea una inscripción confirmada.
     *
     * @param participante
     * @param evento
     *
     * @return una nueva instancia de InscripcionEvento
     */
    public static InscripcionEvento confirmada(Usuario participante, Evento evento) {
        return InscripcionEvento.crearNueva(participante, evento, null, LocalDateTime.now(),
                EstadoInscripcion.CONFIRMADA);
    }

    /**
     * Crea una inscripción pendiente.
     *
     * @param participante
     * @param evento
     *
     * @return una nueva instancia de InscripcionEvento
     */
    public static InscripcionEvento pendiente(Usuario participante, Evento evento) {
        return InscripcionEvento.crearNueva(participante, evento, LocalDateTime.now(), null,
                EstadoInscripcion.PENDIENTE);
    }
}
