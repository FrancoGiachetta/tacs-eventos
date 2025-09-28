package tacs.eventos.model.inscripcion;

import com.mongodb.lang.Nullable;
import lombok.*;
import tacs.eventos.model.Evento;
import tacs.eventos.model.Usuario;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class InscripcionEvento {
    @Getter
    private String id = UUID.randomUUID().toString();
    @Getter
    private final Usuario participante;
    @Getter
    private final Evento evento;
    // TODO: agregar cuando este definido
    // private DatosDePago datosDePago;
    @Nullable
    private final LocalDateTime fechaHoraIngresoAWaitlist;
    @Getter
    private LocalDateTime fechahoraConfirmacion = LocalDateTime.now();
    @Nullable
    @Setter
    private LocalDateTime fechaHoraCancelacion;
    // TODO: agregar cuando este definido
    // private Optional<String> errorDePago;

    @Getter
    @NonNull
    private EstadoInscripcion estado;

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
