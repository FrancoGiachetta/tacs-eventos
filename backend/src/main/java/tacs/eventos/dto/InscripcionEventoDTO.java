package tacs.eventos.dto;

import jakarta.validation.constraints.NotNull;
import tacs.eventos.model.Evento;
import tacs.eventos.model.inscripcion.EstadoInscripcion;

public record InscripcionEventoDTO(@NotNull Evento evento, @NotNull EstadoInscripcion estado) {
}
