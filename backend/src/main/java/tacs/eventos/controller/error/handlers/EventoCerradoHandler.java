package tacs.eventos.controller.error.handlers;

import tacs.eventos.model.evento.Evento;

public class EventoCerradoHandler extends RuntimeException {
    public EventoCerradoHandler(Evento evento) {
        super("El evento " + evento.getTitulo() + " se encuentra cerrado");
    }
}
