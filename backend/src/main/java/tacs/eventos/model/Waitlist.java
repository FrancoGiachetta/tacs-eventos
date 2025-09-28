package tacs.eventos.model;

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
     * Saca y devuelve la próxima inscripción en la waitlist, o un Optional vacío si la waitlist está vacía.
     */
    Optional<String> proximo();
}
