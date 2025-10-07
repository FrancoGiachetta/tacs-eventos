package tacs.eventos.controller.error.handlers;

public class AccesoNoAutorizadoHandler extends RuntimeException {
    public AccesoNoAutorizadoHandler(String mensaje) {
        super(mensaje);
    }
}
