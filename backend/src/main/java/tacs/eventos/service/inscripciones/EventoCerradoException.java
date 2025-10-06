package tacs.eventos.service.inscripciones;

import tacs.eventos.model.evento.Evento;

public class EventoCerradoException extends RuntimeException {
    public final Evento evento;

    public EventoCerradoException(Evento evento) {
        super();
        this.evento = evento;
    }
}
