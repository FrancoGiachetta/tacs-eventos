package tacs.eventos.dto;

import jakarta.validation.constraints.NotBlank;

public record EventoEstadoDTO(@NotBlank boolean abierto) {
}
