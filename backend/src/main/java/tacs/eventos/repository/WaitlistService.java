package tacs.eventos.repository;

import tacs.eventos.model.Evento;
import tacs.eventos.model.waitlist.RedisWaitlist;

public interface WaitlistService {
    /**
     * Obtiene la waitlist de un evento.
     */
    RedisWaitlist waitlist(Evento evento);
}
