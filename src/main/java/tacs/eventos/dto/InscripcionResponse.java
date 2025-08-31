package tacs.eventos.dto;

public record InscripcionResponse(String eventoId, EstadoInscripcionResponse estado) {

    /**
     * @param eventoId
     *
     * @return una inscripción para ese evento, en estado CONFIRMADOA
     */
    public static InscripcionResponse confirmada(String eventoId) {
        return new InscripcionResponse(eventoId, EstadoInscripcionResponse.CONFIRMADA);
    }

    /**
     * @param eventoId
     *
     * @return una inscripción para ese evento, en estado EN_WAITLIST
     */
    public static InscripcionResponse enWaitlist(String eventoId) {
        return new InscripcionResponse(eventoId, EstadoInscripcionResponse.PENDIENTE);
    }
}
