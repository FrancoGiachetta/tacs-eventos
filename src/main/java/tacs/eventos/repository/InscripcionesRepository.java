package tacs.eventos.repository;

import tacs.eventos.model.Evento;
import tacs.eventos.model.InscripcionEvento;
import java.util.List;
import java.util.Optional;

public interface InscripcionesRepository {

    List<InscripcionEvento> todos();

    // TODO: si terminamos modelando usuarios, cambiar participanteId por la entidad Usuario
    Optional<InscripcionEvento> getInscripcion(String participanteId, Evento evento);

    List<InscripcionEvento> getInscripcionesPorParticipante(String participanteId);

    List<InscripcionEvento> getInscripcionesPorEvento(Evento evento);

    void guardarInscripcion(InscripcionEvento inscripcion);

    void eliminarInscripcion(InscripcionEvento inscripcion);

}
