package tacs.eventos.dto;

import tacs.eventos.model.inscripcion.EstadoInscripcion;
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

    /**
     * Sobrecarga con datos extra
     */
    public static InscripcionResponse confirmada(String eventoId, InscripcionEvento inscripcion) {
        return new InscripcionResponse(eventoId, EstadoInscripcionResponse.CONFIRMADA,
                inscripcion.getParticipante().getEmail(), inscripcion.getFechahoraConfirmacion().orElse(null),
                inscripcion.getId());
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

    /**
     * Sobrecarga con email
     */
    public static InscripcionResponse enWaitlist(String eventoId, InscripcionEvento inscripcion) {
        return new InscripcionResponse(eventoId, EstadoInscripcionResponse.PENDIENTE,
                inscripcion.getParticipante().getEmail(), inscripcion.getFechahoraConfirmacion().orElse(null),
                inscripcion.getId());
    }

    // TODO: esto en realidad debería realizarlo un mapper
    public static InscripcionResponse fromInscripcion(InscripcionEvento inscripcion) {
        /*
         * En el campo fechaInscripcion, retorna la fecha en la que la inscripción se confirmó, o, si no fue confirmada,
         * la fecha en la que pasó a waitlist
         */
        return new InscripcionResponse(inscripcion.getEvento().getId(), mapEstado(inscripcion.getEstado()),
                inscripcion.getParticipante().getEmail(),
                inscripcion.getFechahoraConfirmacion().or(inscripcion::getFechaHoraIngresoAWaitlist).orElse(null),
                inscripcion.getId());
    }

    private static EstadoInscripcionResponse mapEstado(EstadoInscripcion estado) {
        return switch (estado) {
        case CONFIRMADA -> EstadoInscripcionResponse.CONFIRMADA;
        case CANCELADA -> EstadoInscripcionResponse.CANCELADA;
        case PENDIENTE -> EstadoInscripcionResponse.PENDIENTE;
        };
    }
}
