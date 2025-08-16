package tacs.eventos.repository;

import tacs.eventos.model.Evento;
import tacs.eventos.model.InscripcionEvento;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InscripcionesInMemoryRepo implements InscripcionesRepository{
  private List<InscripcionEvento> inscripciones;

  public InscripcionesInMemoryRepo() {
    this.inscripciones = new ArrayList<>();
  }
  @Override
  public Optional<InscripcionEvento> getInscripcion(String participanteId, Evento evento) {
    return this.inscripciones
        .stream().filter(i -> i.getEvento().equals(evento) && i.getParticipanteId().equals(participanteId))
        .findFirst();
  }

  @Override
  public List<InscripcionEvento> getInscripcionesPorParticipante(String participanteId) {
    return this.inscripciones
        .stream().filter(i -> i.getParticipanteId().equals(participanteId))
        .toList();
  }

  @Override
  public List<InscripcionEvento> getInscripcionesPorEvento(Evento evento) {
    return this.inscripciones
        .stream().filter(i -> i.getEvento().equals(evento))
        .toList();
  }

  @Override
  public void guardarInscripcion(InscripcionEvento inscripcion) {
    this.inscripciones.add(inscripcion);
  }

  @Override
  public void eliminarInscripcion(InscripcionEvento inscripcion) {
    this.inscripciones.remove(inscripcion);
  }
}
