package tacs.eventos.controller.error.handlers;

public class RecursoNoEncontradoHandler extends RuntimeException {
    public RecursoNoEncontradoHandler(String mensaje) {
        super(mensaje);
    }
}
