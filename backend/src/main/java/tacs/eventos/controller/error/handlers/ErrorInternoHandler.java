package tacs.eventos.controller.error.handlers;

public class ErrorInternoHandler extends RuntimeException {
    public ErrorInternoHandler(String mensaje, Throwable error) {
        super(mensaje, error);
    }
}
