package tacs.eventos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EventoEstadoDTO(@NotNull boolean abierto) {
}
