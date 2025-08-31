package tacs.eventos.dto;

import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

public record InscripcionEnWaitlistDTO(
    @NotEmpty UsuarioResponse usuario,
    @NotEmpty LocalDateTime fechaIngreso
) {
}
