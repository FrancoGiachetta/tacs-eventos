package tacs.eventos.model.inscripcion;

import tacs.eventos.model.Evento;
import tacs.eventos.model.Usuario;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Clase usada para crear instancias de InscripcionEvento.
 */
public class InscripcionFactory {

    /**
     * Crea directamtente una inscripción confirmada, sin tomar información del ítem de la waitlist que dió lugar a la
     * misma.
     *
     * @param participante
     * @param evento
     *
     * @return una nueva instancia de InscripcionEvento
     */
    public static InscripcionEvento confirmada(Usuario participante, Evento evento) {
        return new InscripcionEvento(participante, evento, Optional.empty(), EstadoInscripcion.CONFIRMADA);
    }

    /**
     * Crea una inscripción creada a partir de un ítem de la waitlist, tomando a partir del mismo la información
     * necesaria.
     *
     * @param participante
     * @param evento
     *
     * @return una nueva instancia de InscripcionEvento
     */
    public static InscripcionEvento pendiente(Usuario participante, Evento evento) {
        return new InscripcionEvento(participante, evento, Optional.of(LocalDateTime.now()),
                EstadoInscripcion.PENDIENTE);
    }
}
