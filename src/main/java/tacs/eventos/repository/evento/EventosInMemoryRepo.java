package tacs.eventos.repository.evento;

import org.springframework.stereotype.Repository;
import tacs.eventos.model.Evento;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("eventosInMemoryRepo")
public class EventosInMemoryRepo implements EventosRepository {
    private final List<Evento> eventos;

    public EventosInMemoryRepo() {
        this.eventos = new ArrayList<>();
    }

    @Override
    public List<Evento> todos() {
        return this.eventos;
    }

    @Override
    public Optional<Evento> getEvento(String eventoId) {
        return this.eventos.stream().filter(e -> e.getId().equals(eventoId)).findFirst();
    }

    @Override
    public List<Evento> getEventosPorOrganizador(String organizadorId) {
        return this.eventos.stream().filter(e -> true) // TODO: Completar luego cuando este el organizador en el evento
                .toList();
    }

    @Override
    public List<Evento> getEventosPorCategoria(String categoria) {
        return this.eventos.stream().filter(e -> e.getCategoria().equals(categoria)).toList();
    }

    @Override
    public void guardarEvento(Evento evento) {
        if (!this.eventos.contains(evento)) {
            this.eventos.add(evento);
        }
    }

    @Override
    public void eliminarEvento(Evento evento) {
        this.eventos.remove(evento);
    }
}
