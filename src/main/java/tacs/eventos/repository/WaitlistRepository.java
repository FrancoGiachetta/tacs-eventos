package tacs.eventos.repository;

import tacs.eventos.model.Evento;
import tacs.eventos.model.Usuario;
import tacs.eventos.model.Waitlist;

import java.util.List;

public interface WaitlistRepository {

    /**
     * Obtiene la waitlist de un evento. Si no existe, la crea, la guarda, y la retorna.
     */
    Waitlist waitlist(Evento evento);

    List<Evento> eventosEnCuyasWaitlistEsta(Usuario usuario);
}
