package tacs.eventos.dto;

public record InscripcionResponse(String participanteId, String eventoId, EstadoInscripcionResponse estado) {

    public static InscripcionResponse confirmada(String participanteId, String eventoId) {
        return new InscripcionResponse(participanteId, eventoId, EstadoInscripcionResponse.CONFIRMADA);
    }

    public static InscripcionResponse enWaitlist(String participanteId, String eventoId) {
        return new InscripcionResponse(participanteId, eventoId, EstadoInscripcionResponse.EN_WAITLIST);
    }
}
