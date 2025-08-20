package tacs.eventos.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Optional;

public class InscripcionEvento {
    // TODO: Cambiar por entidad Usuario si modelamos usuarios
    @Getter
    private String participanteId;
    @Getter
    private Evento evento;
    // TODO: agregar cuando este definido
    // private DatosDePago datosDePago;
    @Getter
    private LocalDateTime fechaHoraInscripcion;
    @Getter
    private Optional<LocalDateTime> fechahoraConfirmacion;
    @Getter
    private Optional<LocalDateTime> fechaHoraCancelacion;
    @Getter
    private Optional<String> errorDePago;

    public InscripcionEvento(String participanteId, Evento evento) {
        this.participanteId = participanteId;
        this.evento = evento;
        this.fechaHoraInscripcion = LocalDateTime.now();
        this.fechaHoraCancelacion = Optional.empty();
        this.fechahoraConfirmacion = Optional.empty();
        this.errorDePago = Optional.empty();
    }

    public void setFechahoraConfirmacion(LocalDateTime fechaHoraConfirmacion) {
        this.fechahoraConfirmacion = Optional.of(fechaHoraConfirmacion);
    }

    public void setFechaHoraCancelacion(LocalDateTime fechaHoraCancelacion) {
        this.fechahoraConfirmacion = Optional.of(fechaHoraCancelacion);
    }

    public void setErrorDePago(String error) {
        this.errorDePago = Optional.of(error);
    }

    public String getEstado() {
        if (!fechaHoraCancelacion.isEmpty()) {
            return "Cancelada";
        } else if (!fechahoraConfirmacion.isEmpty()) {
            return "Confirmada";
        } else {
            return "En Waitlist";
        }
    }
}
