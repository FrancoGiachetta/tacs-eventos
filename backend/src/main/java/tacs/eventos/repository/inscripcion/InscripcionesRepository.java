package tacs.eventos.repository.inscripcion;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tacs.eventos.model.Evento;
import tacs.eventos.model.Usuario;
import tacs.eventos.model.inscripcion.EstadoInscripcion;
import tacs.eventos.model.inscripcion.InscripcionEvento;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Guarda las inscripciones confirmadas o canceladas.
 */
@Repository
public interface InscripcionesRepository extends MongoRepository<InscripcionEvento, String> {

    /**
     * Busca una inscripción de ese participante, que esté en un estado distinto al pasado por parámetro.
     *
     * @param participante
     * @param estado
     *
     * @return
     */
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
     * Obtiene todas las inscripciones pendientes para ese evento que hayan sido agregadas a la waitlist después de la
     * fecha pasada por parámetro, ordenadas por fecha de adición.
     *
     * @param evento
     * @param fechaHoraIngresoAWaitlist
     *
     * @return
     */
    default List<InscripcionEvento> pendientesPosterioresALaFechaOrdenados(Evento evento,
            LocalDateTime fechaHoraIngresoAWaitlist) {
        return findByEventoAndEstadoAndFechaHoraIngresoAWaitlistGreaterThanEqualOrderByFechaHoraIngresoAWaitlist(evento,
                EstadoInscripcion.PENDIENTE, fechaHoraIngresoAWaitlist);
    }

    /**
     * Obtiene todas las inscripciones para ese evento, en ese estado, que hayan sido agregadas a la waitlist en una
     * fecha posterior a la pasada por parámetro, ordenadas por fechaHoraIngresoAWaitlist.
     *
     * @param evento
     * @param estado
     * @param fechaHoraIngresoAWaitlist
     *
     * @return
     */
    List<InscripcionEvento> findByEventoAndEstadoAndFechaHoraIngresoAWaitlistGreaterThanEqualOrderByFechaHoraIngresoAWaitlist(
            Evento evento, EstadoInscripcion estado, LocalDateTime fechaHoraIngresoAWaitlist);

    /**
     * Obtiene todas las inscripciones para ese evento, en ese estado, ordenadas por fechaHoraIngresoAWaitlist.
     *
     * @param evento
     * @param estado
     *
     * @return
     */
    List<InscripcionEvento> findByEventoAndEstadoOrderByFechaHoraIngresoAWaitlist(Evento evento,
            EstadoInscripcion estado);

    /**
     * @param evento
     *
     * @return cantidad de inscripciones confirmadas para ese evento
     */
    int countByEvento(Evento evento);

    /**
     * Busca una inscripción que no esté cancelada (puede estar confirmada o pendiente) para ese participante y evento.
     *
     * @param usuarioInscripto
     * @param evento
     *
     * @return la inscripción, o un Optional vacío si no existe una que no cumpla con las condiciones
     */
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

    /**
     * @param evento
     * @param estadoInscripcion
     *
     * @return cantidad de inscripciones en ese estado para ese evento
     */
    int countByEventoAndEstado(Evento evento, EstadoInscripcion estadoInscripcion);

    Optional<InscripcionEvento> findFirstByEventoAndEstado(Evento evento, EstadoInscripcion estado);

    // TODO: este creo que es el único método del repo que tendría sentido probar en un test unitario

    /**
     * @return todos los eventos que tengan inscripciones creadas (en cualquier estado)
     */
    @Aggregation(pipeline = { "{ '$group': { '_id': '$evento' } }", "{ '$replaceRoot': { 'newRoot': '$_id' } }" })
    Stream<Evento> eventosConInscripciones();
}
