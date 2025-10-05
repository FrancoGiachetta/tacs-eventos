package tacs.eventos.model.inscripcion;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import tacs.eventos.model.Evento;
import tacs.eventos.model.Usuario;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class InscripcionEvento {
    private String id = UUID.randomUUID().toString();
    private final Usuario participante;
    private final Evento evento;
    // TODO: agregar cuando este definido
    // private DatosDePago datosDePago;
    private final Optional<LocalDateTime> fechaHoraIngresoAWaitlist;
    private LocalDateTime fechahoraConfirmacion = LocalDateTime.now();
    private Optional<LocalDateTime> fechaHoraCancelacion = Optional.empty();
    // TODO: agregar cuando este definido
    // private Optional<String> errorDePago;

    @Getter
    @NonNull
    private EstadoInscripcion estado;

    private void setFechaHoraCancelacion(LocalDateTime fechaHoraCancelacion) {
        this.fechaHoraCancelacion = Optional.of(fechaHoraCancelacion);
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

    /**
     * Confirma la inscripción si está pendiente.
     */
    public void confirmar() {
        if (estaPendiente()) {
            estado = EstadoInscripcion.CONFIRMADA;
            fechahoraConfirmacion = LocalDateTime.now();
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
