package tacs.eventos.model.waitlist;

import tacs.eventos.model.inscripcion.InscripcionEvento;

import java.util.Optional;

/**
 * Representa una cola ordenada por orden de llegada, de inscripciones pendientes para un evento.
 */
public interface Waitlist {
    /**
     * Agrega a la waitlist una inscripción.
     *
     * @param idInscripcion id de una inscripción pendiente que ya fue registrada en el sistema
     */
    void agregar(String idInscripcion);

    /**
     * Devuelve la próxima inscripción pendiente en la waitlist.
     */
    Optional<InscripcionEvento> proxima();
}
