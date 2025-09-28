package tacs.eventos.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tacs.eventos.model.Evento;
import tacs.eventos.model.Waitlist;
import tacs.eventos.repository.WaitlistRepository;
import tacs.eventos.repository.evento.EventosRepository;
import tacs.eventos.repository.inscripcion.InscripcionesRepository;

@Service
@AllArgsConstructor
public class EstadisticaService {

    private final EventosRepository eventosRepository;
    private final InscripcionesRepository inscripcionesRepository;
    private final WaitlistRepository waitlistRepository;

    public int cantidadInscribiciones() {
        return this.inscripcionesRepository.todos().size();
    }

    public int cantidadEventos() throws Exception {
        return Math.toIntExact(this.eventosRepository.count());
    }

    // TODO: falta chequear si esta bien aplicado esta logica que pide de tasa de conversion de waitList
    public int calcularTasaConversionWL(String id) {
        Evento evento = this.eventosRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento no encontrado"));
        int calculoTasa = 0;

        if (evento.isAbierto()) {
            int TotalInscripcionesEvento;
            Waitlist eventoWaitlist;
            int totalInscripcionesEnWaitlist;
            TotalInscripcionesEvento = this.inscripcionesRepository.getInscripcionesPorEvento(evento).size();
            eventoWaitlist = this.waitlistRepository.waitlist(evento);
            totalInscripcionesEnWaitlist = eventoWaitlist.cantidadEnCola(); // TODO: reemplazar por llamada a repo
            calculoTasa = (TotalInscripcionesEvento / totalInscripcionesEnWaitlist) * 100;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "El evento ya fue cerrado");
        }

        return calculoTasa;

    }

}
