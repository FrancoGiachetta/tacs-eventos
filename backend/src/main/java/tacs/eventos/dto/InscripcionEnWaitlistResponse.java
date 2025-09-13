package tacs.eventos.dto;

import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

public record InscripcionEnWaitlistResponse(UsuarioResponse usuario, LocalDateTime fechaIngreso) {
}
