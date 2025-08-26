package tacs.eventos.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import tacs.eventos.model.Evento;
import tacs.eventos.repository.EventosRepository;
import tacs.eventos.repository.InscripcionesRepository;

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

    public int cantidadInscribiciones() {
        return this.inscripcionesRepository.totalInscripciones();
    }


    public int cantidadEventos() {
        return this.eventosRepository.cantidaEventos();
    }


    public int calcularTasaConversionWL(String id) {
        Optional<Evento> evento = this.eventosRepository.getEvento(id);

        int calculoTasa=0;

        int totalInscriptos;
        int totalEnWaitList;
        if (evento.isPresent()){
             totalInscriptos = evento.get().getInscritos();
             totalEnWaitList = evento.get().getWaitlist().size();
            calculoTasa=(totalInscriptos / totalEnWaitList) *100;

        }

        return calculoTasa;
    }
}
