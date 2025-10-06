package tacs.eventos.dto.errores;

import java.time.LocalDateTime;

public record ErrorResponse(String mensaje, LocalDateTime momento) {

    public ErrorResponse(String mensaje) {
        this(mensaje, LocalDateTime.now());
    }
}
