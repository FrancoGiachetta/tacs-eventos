package tacs.eventos.repository;

import tacs.eventos.model.Evento;
import tacs.eventos.model.Waitlist;

public interface WaitlistRepository {

    /**
     * Obtiene la waitlist de un evento. Si no existe, la crea, la guarda, y la retorna.
     */
    Waitlist waitlist(Evento evento);
}
