package tacs.eventos.repository;

import tacs.eventos.model.Evento;
import tacs.eventos.model.RedisWaitlist;

public interface WaitlistService {
    /**
     * Obtiene la waitlist de un evento.
     */
    RedisWaitlist waitlist(Evento evento);
}
