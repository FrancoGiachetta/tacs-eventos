package tacs.eventos.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import tacs.eventos.model.Evento;
import tacs.eventos.model.Waitlist;
import tacs.eventos.repository.WaitlistRepository;
import tacs.eventos.repository.evento.EventosRepository;
import tacs.eventos.repository.inscripcion.InscripcionesRepository;
import java.util.Optional;
import org.apache.logging.log4j.*;

@Service
public class EstadisticaService {

    private final EventosRepository eventosRepository;
    private final InscripcionesRepository inscripcionesRepository;
    private final WaitlistRepository waitlistRepository;

    public EstadisticaService(@Qualifier("eventosInMemoryRepo") EventosRepository eventosRepository,
            @Qualifier("inscripcionesInMemoryRepo") InscripcionesRepository inscripcionesRepository,
            @Qualifier("waitlistsInMemoryRepo") WaitlistRepository waitlistRepository) {
        this.eventosRepository = eventosRepository;
        this.inscripcionesRepository = inscripcionesRepository;
        this.waitlistRepository = waitlistRepository;
    }

    // TODO: la query deberia ir a la base count sobre inscripciones para tener una mejor performance

    public int cantidadInscribiciones() {
        return this.inscripcionesRepository.todos().size();
    }

    public int cantidadEventos() {
        return this.eventosRepository.cantidaEventos();
    }

    // TODO: falta chequear si esta bien aplicado esta logica que pide de tasa de conversion de waitList
    public int calcularTasaConversionWL(String id) {
        Optional<Evento> evento = this.eventosRepository.getEvento(id);

        int calculoTasa = 0;

        int TotalInscripcionesEvento;
        Waitlist totalEnWaitList;
        int totalWL;
        if (evento.isPresent() && evento.get().isAbierto()) {
            TotalInscripcionesEvento = this.inscripcionesRepository.getInscripcionesPorEvento(evento.get()).size();
            totalEnWaitList = this.waitlistRepository.waitlist(evento.get());
            totalWL = totalEnWaitList.getItems().size();
            calculoTasa = (TotalInscripcionesEvento / totalWL) * 100;
        }
        return calculoTasa;
    }
}
