package tacs.eventos.repository;

import tacs.eventos.model.Evento;
import tacs.eventos.model.Usuario;
import tacs.eventos.model.inscripcion.InscripcionEvento;

import java.util.List;
import java.util.Optional;

/**
 * Guarda las inscripciones confirmadas o canceladas.
 */
public interface InscripcionesRepository {

    List<InscripcionEvento> todos();

    Optional<InscripcionEvento> getInscripcion(Usuario participante, Evento evento);

    List<InscripcionEvento> getInscripcionesPorParticipante(Usuario participante);

    List<InscripcionEvento> getInscripcionesPorEvento(Evento evento);

    /**
     * Guarda una inscripcion si esta todavía no existe. NO USAR DIRECTAMENTE, USAR InscripcionService.
     */
    void guardarInscripcion(InscripcionEvento inscripcion);

    void eliminarInscripcion(InscripcionEvento inscripcion);

    int cantidadInscriptos(Evento evento);
}
