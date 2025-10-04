package tacs.eventos.dto;

import tacs.eventos.model.inscripcion.EstadoInscripcion;

public class EstadoInscripcionMapper {
    public static EstadoInscripcionResponse mapEstado(EstadoInscripcion estado) {
        return switch (estado) {
        case CONFIRMADA -> EstadoInscripcionResponse.CONFIRMADA;
        case CANCELADA -> EstadoInscripcionResponse.CANCELADA;
        case PENDIENTE -> EstadoInscripcionResponse.PENDIENTE;
        };
    }
}
