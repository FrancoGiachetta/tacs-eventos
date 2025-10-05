package tacs.eventos.dto.errores;

import java.time.LocalDateTime;

public class ErrorResponse {
    private String mensaje;
    private int estado;
    private LocalDateTime momento;

    public ErrorResponse(String mensaje, int estado) {
        this.mensaje = mensaje;
        this.estado = estado;
        this.momento = LocalDateTime.now();
    }
}
