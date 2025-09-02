package tacs.eventos.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
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

    public int cantidadEventos() throws Exception {
        //


        try {

            return this.eventosRepository.cantidaEventos();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno en servidor", e);
        }

    }

    // TODO: falta chequear si esta bien aplicado esta logica que pide de tasa de conversion de waitList
    public int calcularTasaConversionWL(String id) {
        Optional<Evento> evento = this.eventosRepository.getEvento(id);
        int calculoTasa = 0;


        try {
            if (evento.isPresent() && evento.get().isAbierto()) {
                int TotalInscripcionesEvento;
                Waitlist eventoWaitlist;
                int totalInscripcionesEnWaitlist;
                TotalInscripcionesEvento = this.inscripcionesRepository.getInscripcionesPorEvento(evento.get()).size();
                eventoWaitlist = this.waitlistRepository.waitlist(evento.get());
                totalInscripcionesEnWaitlist = eventoWaitlist.getItems().size();
                calculoTasa = (TotalInscripcionesEvento / totalInscripcionesEnWaitlist) * 100;
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno en servidor", e);
        }

        return calculoTasa;

    }

}

