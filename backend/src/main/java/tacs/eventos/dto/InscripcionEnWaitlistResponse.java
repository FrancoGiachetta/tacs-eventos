package tacs.eventos.dto;

import java.time.LocalDateTime;

public record InscripcionEnWaitlistResponse(String id, UsuarioResponse usuario, LocalDateTime fechaIngreso) {
}
