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

    /**
     * @param usuario
     *
     * @return eventos para los cuales el usuario está en la waitlist
     */
    List<Evento> eventosEnCuyasWaitlistEsta(Usuario usuario);
}
