package tacs.eventos.repository.inscripcion;

import tacs.eventos.model.Evento;
import tacs.eventos.model.Usuario;
import tacs.eventos.model.inscripcion.InscripcionEvento;

import java.util.List;
import java.util.Optional;

/**
 * Guarda las inscripciones confirmadas o canceladas.
 */
public interface InscripcionesRepository {

    /**
     * @return todas las inscripciones para todos los eventos, que estén canceladas o confirmadas.
     */
    List<InscripcionEvento> todos();

    /**
     * @param participante
     * @param evento
     * @return la inscripción (cancelada o confirmada) de un participante a un evento, si es que esta existe
     */
    Optional<InscripcionEvento> getInscripcionConfirmada(Usuario participante, Evento evento);

    /**
     * @param participante
     * @return todas las inscripciones (canceladas o confirmadas) de ese participante
     */
    List<InscripcionEvento> getInscripcionesPorParticipante(Usuario participante);

    /**
     * @param evento
     * @return todas las inscripciones (canceladas o confirmadas) de ese evento
     */
    List<InscripcionEvento> getInscripcionesPorEvento(Evento evento);

    /**
     * Guarda una inscripcion si esta todavía no existe. NO USAR DIRECTAMENTE, USAR InscripcionService.
     */
    void guardarInscripcion(InscripcionEvento inscripcion);

    /**
     * @param evento
     * @return cantidad de inscripciones confirmadas para ese evento
     */
    int cantidadInscriptos(Evento evento);
}
