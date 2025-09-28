package tacs.eventos.dto;

import java.time.LocalDateTime;

public record InscripcionEnWaitlistResponse(UsuarioResponse usuario, LocalDateTime fechaIngreso) {
}
