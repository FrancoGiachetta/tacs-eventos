package tacs.eventos.service;

import tacs.eventos.model.evento.Evento;
import tacs.eventos.model.waitlist.Waitlist;

public interface WaitlistService {
    /**
     * Obtiene la waitlist de un evento.
     */
    Waitlist waitlist(Evento evento);
}
