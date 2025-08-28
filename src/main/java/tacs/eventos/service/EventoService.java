package tacs.eventos.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tacs.eventos.model.Evento;
import tacs.eventos.repository.EventosRepository;
import tacs.eventos.repository.InscripcionesRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EventoService {
    private final EventosRepository eventosRepository;
    private final InscripcionesRepository inscripcionesRepository;

    public Evento crearEvento(Evento evento) {
        eventosRepository.guardarEvento(evento);
        return evento;
    }

    public List<Evento> listarEventos() {
        return this.eventosRepository.todos();
    }

    public Optional<Evento> buscarEventoPorId(String id) {
        return this.eventosRepository.getEvento(id);
    }

}
