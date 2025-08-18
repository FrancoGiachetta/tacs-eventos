package tacs.eventos.model;

import jdk.jfr.Event;
import java.time.LocalDateTime;
import java.util.Optional;

public class InscripcionEvento {
    // TODO: Cambiar por entidad Usuario si modelamos usuarios
    private String participanteId;
    private Evento evento;
    // TODO: agregar cuando este definido
    // private DatosDePago datosDePago;
    private LocalDateTime fechaHoraInscripcion;
    private Optional<LocalDateTime> fechahoraConfirmacion;
    private Optional<LocalDateTime> fechaHoraCancelacion;
    private Optional<String> errorDePago;

    public InscripcionEvento(String participanteId, Evento evento) {
        this.participanteId = participanteId;
        this.evento = evento;
        this.fechaHoraInscripcion = LocalDateTime.now();
        this.fechaHoraCancelacion = Optional.empty();
        this.fechahoraConfirmacion = Optional.empty();
        this.errorDePago = Optional.empty();
    }

    public String getParticipanteId() {
        return participanteId;
    }

    public Evento getEvento() {
        return evento;
    }

    public LocalDateTime getFechaHoraInscripcion() {
        return fechaHoraInscripcion;
    }

    public Optional<LocalDateTime> getFechahoraConfirmacion() {
        return fechahoraConfirmacion;
    }

    public Optional<LocalDateTime> getFechaHoraCancelacion() {
        return fechaHoraCancelacion;
    }

    public Optional<String> getErrorDePago() {
        return errorDePago;
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
