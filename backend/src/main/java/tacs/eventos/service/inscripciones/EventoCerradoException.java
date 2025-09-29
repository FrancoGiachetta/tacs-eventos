package tacs.eventos.service.inscripciones;

import tacs.eventos.model.Evento;

public class EventoCerradoException extends Exception {
    public final Evento evento;

    public EventoCerradoException(Evento evento) {
        super();
        this.evento = evento;
    }
}
