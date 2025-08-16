package tacs.eventos.repository;

import tacs.eventos.model.Evento;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EventosInMemoryRepo implements EventosRepository {
  private List<Evento> eventos;

  public EventosInMemoryRepo() {
    this.eventos = new ArrayList<>();
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
    return this.eventos.stream().filter(e -> e.getCategoria().equals(categoria))
        .toList();
  }

  @Override
  public void guardarEvento(Evento evento) {
    this.eventos.add(evento);
  }

  @Override
  public void eliminarEvento(Evento evento) {
    this.eventos.remove(evento);
  }
}
