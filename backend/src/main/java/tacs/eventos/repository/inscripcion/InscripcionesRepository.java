package tacs.eventos.repository.inscripcion;

import org.springframework.data.mongodb.repository.MongoRepository;
import tacs.eventos.model.Evento;
import tacs.eventos.model.Usuario;
import tacs.eventos.model.inscripcion.EstadoInscripcion;
import tacs.eventos.model.inscripcion.InscripcionEvento;

import java.util.List;
import java.util.Optional;

/**
 * Guarda las inscripciones confirmadas o canceladas.
 */
public interface InscripcionesRepository extends MongoRepository<InscripcionEvento, String> {

    List<InscripcionEvento> findByParticipanteAndEstadoNot(Usuario participante, EstadoInscripcion estado);

    Optional<InscripcionEvento> findFirstByParticipanteAndEventoAndEstadoNot(Usuario participante, Evento evento,
            EstadoInscripcion estado);

    /**
     * @param evento
     *
     * @return todas las inscripciones (confirmadas, canceladas, o pendientes) de ese evento
     */
    List<InscripcionEvento> findByEventoAndEstado(Evento evento, EstadoInscripcion estado);

    /**
     * @param evento
     *
     * @return cantidad de inscripciones confirmadas para ese evento
     */
    int countByEvento(Evento evento);

    default Optional<InscripcionEvento> noCanceladaParaParticipanteYEvento(Usuario usuarioInscripto, Evento evento) {
        return findFirstByParticipanteAndEventoAndEstadoNot(usuarioInscripto, evento, EstadoInscripcion.CANCELADA);
    }

    /**
     * @param participante
     *
     * @return las inscripciones no canceladas de ese participante
     */
    default List<InscripcionEvento> noCanceladasDeParticipante(Usuario participante) {
        return findByParticipanteAndEstadoNot(participante, EstadoInscripcion.CANCELADA);
    }
}
