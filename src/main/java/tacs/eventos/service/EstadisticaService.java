package tacs.eventos.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import tacs.eventos.model.Evento;
import tacs.eventos.repository.evento.EventosRepository;
import tacs.eventos.repository.inscripcion.InscripcionesRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EstadisticaService {

    private final EventosRepository eventosRepository;
    private final InscripcionesRepository inscripcionesRepository;
    private final List<Evento> eventos = new ArrayList<>();

    public EstadisticaService(@Qualifier("eventosInMemoryRepo") EventosRepository eventosRepository,
            @Qualifier("inscripcionesInMemoryRepo") InscripcionesRepository inscripcionesRepository) {
        this.eventosRepository = eventosRepository;
        this.inscripcionesRepository = inscripcionesRepository;
    }

    // TODO: la query deberia ir a la base count sobre inscripciones para tener una mejor performance
    public int cantidadInscribiciones() {
        return this.inscripcionesRepository.todos().size();
    }

    public int cantidadEventos() {
        return this.eventosRepository.cantidaEventos();
    }

    public int calcularTasaConversionWL(String id) {
        Optional<Evento> evento = this.eventosRepository.getEvento(id);

        // todo:desarrollar metodo
        return 1;
    }
}
