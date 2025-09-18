package tacs.eventos.dto;

import tacs.eventos.model.inscripcion.InscripcionEvento;

import java.time.LocalDateTime;

public record InscripcionResponse(String eventoId, EstadoInscripcionResponse estado, String email,
        LocalDateTime fechaInscripcion, String id

) {
    // Constructor de compatibilidad: permite seguir haciendo new InscripcionResponse(eventoId, estado)
    public InscripcionResponse(String eventoId, EstadoInscripcionResponse estado) {
        this(eventoId, estado, null, null, null);
    }

    /**
     * @param eventoId
     *
     * @return una inscripción para ese evento, en estado CONFIRMADA
     */
    public static InscripcionResponse confirmada(String eventoId) {
        return new InscripcionResponse(eventoId, EstadoInscripcionResponse.CONFIRMADA);
    }

    /** Sobrecarga con datos extra */
    public static InscripcionResponse confirmada(String eventoId, InscripcionEvento inscripcion) {
        return new InscripcionResponse(eventoId, EstadoInscripcionResponse.CONFIRMADA,
                inscripcion.getParticipante().getEmail(), inscripcion.getFechahoraConfirmacion(), inscripcion.getId());
    }

    /**
     * @param eventoId
     *            id del evento
     *
     * @return una inscripción para ese evento, en estado PENDIENTE
     */
    public static InscripcionResponse enWaitlist(String eventoId) {
        return new InscripcionResponse(eventoId, EstadoInscripcionResponse.PENDIENTE);
    }

    /** Sobrecarga con email */
    public static InscripcionResponse enWaitlist(String eventoId, InscripcionEvento inscripcion) {
        return new InscripcionResponse(eventoId, EstadoInscripcionResponse.PENDIENTE,
                inscripcion.getParticipante().getEmail(), inscripcion.getFechahoraConfirmacion(), inscripcion.getId());
    }
}
