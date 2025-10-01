package tacs.eventos.model.inscripcion;

import com.mongodb.lang.Nullable;
import lombok.*;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import tacs.eventos.model.Evento;
import tacs.eventos.model.Usuario;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/* Constructor de todos los argumentos, para que use Spring Data para crear objetos desde documentos de la DB */
@AllArgsConstructor(access = AccessLevel.PROTECTED, onConstructor_ = @PersistenceCreator)
@Document(collection = "inscripciones")
@CompoundIndex(name = "evento_participante_estado_idx", def = "{'evento': 1, 'participante': 1, 'estado': 1}")
@CompoundIndex(name = "participante_estado_idx", def = "{'participante': 1, 'estado': 1}")
@CompoundIndex(name = "evento_estado_idx", def = "{'evento': 1, 'estado': 1}")
public class InscripcionEvento {
    @Getter
    @NonNull
    @Indexed(unique = true)
    private String id;
    @Getter
    @NonNull
    private final Usuario participante;
    @Getter
    @NonNull
    @Indexed
    private final Evento evento;
    @Nullable
    private final LocalDateTime fechaHoraIngresoAWaitlist;
    @Setter
    @Nullable
    private LocalDateTime fechahoraConfirmacion;
    @Nullable
    @Setter
    private LocalDateTime fechaHoraCancelacion;
    @Getter
    @NonNull
    private EstadoInscripcion estado;

    /**
     * NO USAR, PUBLICA PARA TESTING, usar los métod0s de la clase InscripcionFactory.
     * Crea una nueva inscripción.
     *
     * @param participante
     * @param evento
     * @param fechaHoraIngresoAWaitlist
     * @param fechaHoraConfirmacion
     * @param estado
     * @return
     */
    public static InscripcionEvento crearNueva(Usuario participante, Evento evento,
                                               LocalDateTime fechaHoraIngresoAWaitlist, LocalDateTime fechaHoraConfirmacion, EstadoInscripcion estado) {
        return new InscripcionEvento(UUID.randomUUID().toString(), participante, evento, fechaHoraIngresoAWaitlist,
                fechaHoraConfirmacion, null, estado);
    }

    /**
     * Cancela la inscripción si no está ya cancelada.
     */
    public void cancelar() {
        if (!estaCancelada()) { // Si no está cancelada
            estado = EstadoInscripcion.CANCELADA;
            setFechaHoraCancelacion(LocalDateTime.now()); // La cancela
        }
    }

    public boolean estaConfirmada() {
        return estado.equals(EstadoInscripcion.CONFIRMADA);
    }

    public boolean estaPendiente() {
        return estado.equals(EstadoInscripcion.PENDIENTE);
    }

    public boolean estaCancelada() {
        return estado.equals(EstadoInscripcion.CANCELADA);
    }

    public Optional<LocalDateTime> getFechaHoraCancelacion() {
        return Optional.ofNullable(fechaHoraCancelacion);
    }

    public Optional<LocalDateTime> getFechaHoraIngresoAWaitlist() {
        return Optional.ofNullable(fechaHoraIngresoAWaitlist);
    }

    public Optional<LocalDateTime> getFechahoraConfirmacion() {
        return Optional.ofNullable(fechahoraConfirmacion);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof InscripcionEvento inscripcion && this.participante.equals(inscripcion.getParticipante())
                && this.evento.equals(inscripcion.getEvento());
    }

    @Override
    public int hashCode() {
        return Objects.hash(participante, evento);
    }

    public void confirmar() {
        if (!estaConfirmada()) { // Si no está confirmada, la confirma
            estado = EstadoInscripcion.CONFIRMADA;
            setFechahoraConfirmacion(LocalDateTime.now());
        }
    }
}
