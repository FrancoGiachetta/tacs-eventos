package tacs.eventos.controller.error.handlers;

public class AccesoDenegadoHandler extends RuntimeException {
    public AccesoDenegadoHandler(String mensaje) {
        super(mensaje);
    }
}
