package tacs.eventos.repository.evento;

import tacs.eventos.model.Evento;
import java.util.List;
import java.util.Optional;

public interface EventosRepository {
    List<Evento> todos();

    Optional<Evento> getEvento(String eventoId);

    // TODO: si vamos a terminar modelando usuarios cambiarlo a la entidad Usuario
    List<Evento> getEventosPorOrganizador(String organizadorId);

    List<Evento> getEventosPorCategoria(String categoria);

    void guardarEvento(Evento evento);

    void eliminarEvento(Evento evento);
}
