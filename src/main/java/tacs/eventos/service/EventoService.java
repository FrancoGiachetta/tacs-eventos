package tacs.eventos.service;

import org.springframework.beans.factory.annotation.Qualifier;
import tacs.eventos.model.Evento;
import org.springframework.stereotype.Service;
import tacs.eventos.model.InscripcionEvento;
import tacs.eventos.repository.evento.EventosRepository;
import tacs.eventos.repository.FiltroBusqueda;
import tacs.eventos.repository.InscripcionesRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EventoService {

    private final EventosRepository eventosRepository;
    private final InscripcionesRepository inscripcionesRepository;
    private final List<Evento> eventos = new ArrayList<>();

    public EventoService(@Qualifier("eventosInMemoryRepo") EventosRepository eventosRepository,
            @Qualifier("inscripcionesInMemoryRepo") InscripcionesRepository inscripcionesRepository) {
        this.eventosRepository = eventosRepository;
        this.inscripcionesRepository = inscripcionesRepository;
    }

    public Evento crearEvento(Evento evento) {
        eventos.add(evento);
        eventosRepository.guardarEvento(evento);
        return evento;
    }

    public List<Evento> listarEventos() {
        return this.eventosRepository.todos();
    }

    public Optional<Evento> buscarEventoPorId(String id) {
        return this.eventosRepository.getEvento(id);
    }

    public List<Evento> filtrarEventos(List<FiltroBusqueda<Evento>> filtros) {
        return this.eventosRepository.filtrarEventos(filtros);
    }

    public boolean inscribirUsuario(String eventoId, String usuarioId) {
        Optional<Evento> evt = this.eventosRepository.getEvento(eventoId);
        var inscripto = evt.map(evento -> evento.agregarParticipante(usuarioId)).orElse(false);
        if (inscripto) {
            var inscripcion = new InscripcionEvento(usuarioId, evt.get());
            this.inscripcionesRepository.guardarInscripcion(inscripcion);
            this.eventosRepository.guardarEvento(evt.get());
        }
        return inscripto;
    }

    public boolean cancelarInscripcion(String eventoId, String usuarioId) {
        Optional<Evento> evt = this.eventosRepository.getEvento(eventoId);
        var cancelado = evt.map(evento -> evento.cancelarParticipante(usuarioId)).orElse(false);
        if (evt.isPresent()) {
            this.eventosRepository.guardarEvento(evt.get());
        }
        return cancelado;
    }
}
