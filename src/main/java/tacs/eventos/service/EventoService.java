package tacs.eventos.service;

import tacs.eventos.model.Evento;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EventoService {

    private final List<Evento> eventos = new ArrayList<>();

    public Evento crearEvento(Evento evento) {
        eventos.add(evento);
        return evento;
    }

    public List<Evento> listarEventos() {
        return eventos;
    }

    public Optional<Evento> buscarEventoPorId(String id) {
        return eventos.stream().filter(e -> e.getId().equals(id)).findFirst();
    }

    public boolean inscribirUsuario(String eventoId, String usuarioId) {
        Optional<Evento> evt = buscarEventoPorId(eventoId);
        return evt.map(evento -> evento.agregarParticipante(usuarioId)).orElse(false);
    }

    public boolean cancelarInscripcion(String eventoId, String usuarioId) {
        Optional<Evento> evt = buscarEventoPorId(eventoId);
        return evt.map(evento -> evento.cancelarParticipante(usuarioId)).orElse(false);
    }
}
