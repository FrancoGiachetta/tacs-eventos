package tacs.eventos.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tacs.eventos.model.Evento;
import tacs.eventos.model.inscripcion.EstadoInscripcion;
import tacs.eventos.repository.evento.EventosRepository;
import tacs.eventos.repository.inscripcion.InscripcionesRepository;

@Service
@AllArgsConstructor
public class EstadisticaService {

    private final EventosRepository eventosRepository;
    private final InscripcionesRepository inscripcionesRepository;

    public long cantidadInscripciones() {
        return this.inscripcionesRepository.count();
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
            int totalInscripcionesEnWaitlist;
            TotalInscripcionesEvento = this.inscripcionesRepository.countByEvento(evento);
            totalInscripcionesEnWaitlist = this.inscripcionesRepository.countByEventoAndEstado(evento, EstadoInscripcion.PENDIENTE);
            calculoTasa = (TotalInscripcionesEvento / totalInscripcionesEnWaitlist) * 100;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "El evento ya fue cerrado");
        }

        return calculoTasa;

    }

}
