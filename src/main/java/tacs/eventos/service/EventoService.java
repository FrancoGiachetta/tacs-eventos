package tacs.eventos.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tacs.eventos.model.Evento;
import tacs.eventos.model.Usuario;
import tacs.eventos.repository.FiltroBusqueda;
import tacs.eventos.repository.evento.EventosRepository;
import tacs.eventos.repository.inscripcion.InscripcionesRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EventoService {
    private final EventosRepository eventosRepository;

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

    public List<Evento> filtrarEventos(List<FiltroBusqueda<Evento>> filtros) {
        return this.eventosRepository.filtrarEventos(filtros);
    }

    public List<Evento> buscarEventosOrganizadosPor(Usuario organizador) {
        return this.eventosRepository.getEventosPorOrganizador(organizador.getId());
    }

    public void cerrarEvento(Usuario organizador, Evento evento) {
        evento.cerrarEvento();
        this.eventosRepository.guardarEvento(evento);
    }

    public void abrirEvento(Usuario organizador, Evento evento) {
        evento.abrirEvento();
        this.eventosRepository.guardarEvento(evento);
    }


}
