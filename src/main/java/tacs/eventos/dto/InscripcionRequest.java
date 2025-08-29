package tacs.eventos.dto;

import jakarta.validation.constraints.NotBlank;

public record InscripcionRequest(@NotBlank String usuarioId, @NotBlank String eventoId) {
}
