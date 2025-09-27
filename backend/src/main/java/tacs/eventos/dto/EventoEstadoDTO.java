package tacs.eventos.dto;

import jakarta.validation.constraints.NotNull;

public record EventoEstadoDTO(@NotNull boolean abierto) {
}
