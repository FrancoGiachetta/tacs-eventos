package tacs.eventos.model.inscripcion;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import tacs.eventos.model.Evento;
import tacs.eventos.model.Usuario;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class InscripcionEvento {
    @Getter
    private final Usuario participante;
    @Getter
    private final Evento evento;
    // TODO: agregar cuando este definido
    // private DatosDePago datosDePago;
    @Getter
    private final Optional<LocalDateTime> fechaHoraIngresoAWaitlist;
    @Getter
    private LocalDateTime fechahoraConfirmacion = LocalDateTime.now();
    @Getter
    private Optional<LocalDateTime> fechaHoraCancelacion = Optional.empty();
    // TODO: agregar cuando este definido
    // private Optional<String> errorDePago;

    private void setFechaHoraCancelacion(LocalDateTime fechaHoraCancelacion) {
        this.fechaHoraCancelacion = Optional.of(fechaHoraCancelacion);
    }

    /**
     * Cancela la inscripción si no está ya cancelada.
     */
    public void cancelar() {
        if (!getEstado().equals(EstadoInscripcion.CANCELADA)) // Si no está cancelada
            setFechaHoraCancelacion(LocalDateTime.now()); // La cancela
    }

    /**
     * @return el estado de la inscripción
     */
    public EstadoInscripcion getEstado() {
        if (fechaHoraCancelacion.isPresent())
            return EstadoInscripcion.CANCELADA;
        else
            return EstadoInscripcion.CONFIRMADA;
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
}
