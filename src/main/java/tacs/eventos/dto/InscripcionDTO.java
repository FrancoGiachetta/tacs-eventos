package tacs.eventos.dto;

import jakarta.validation.constraints.NotBlank;

public record InscripcionDTO(@NotBlank String usuarioId) {
}
