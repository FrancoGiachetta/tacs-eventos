package tacs.eventos.dto;

public record InscripcionResponse(String eventoId, EstadoInscripcionResponse estado) {

    public static InscripcionResponse confirmada(String eventoId) {
        return new InscripcionResponse(eventoId, EstadoInscripcionResponse.CONFIRMADA);
    }

    public static InscripcionResponse enWaitlist(String eventoId) {
        return new InscripcionResponse(eventoId, EstadoInscripcionResponse.EN_WAITLIST);
    }
}
