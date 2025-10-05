package tacs.eventos.service.waitlist.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tacs.eventos.model.Evento;
import tacs.eventos.model.inscripcion.EstadoInscripcion;
import tacs.eventos.model.inscripcion.InscripcionEvento;
import tacs.eventos.model.waitlist.Waitlist;
import tacs.eventos.redis_utils.EstadoInicializacionRedis;
import tacs.eventos.redis_utils.FlagsInicializacionRedis;
import tacs.eventos.repository.inscripcion.InscripcionesRepository;

import java.util.List;

import static tacs.eventos.redis_utils.EstadoInicializacionRedis.INICIALIZANDO;
import static tacs.eventos.redis_utils.EstadoInicializacionRedis.LISTO;

@Service
@RequiredArgsConstructor
public class InicializacionWaitlistRedisService {
    private final InscripcionesRepository inscripcionesRepository;
    private final FlagsInicializacionRedis flagsInicializacion;

    @Async
    void inicializarWaitlist(Evento evento, Waitlist waitlistAInicializar) {
        /* Avisa que está inicializando esta cola, para que ninguna otra instancia de este servicio intente hacerlo */
        flagsInicializacion.setEstadoInicializacion(flagInicializacionCola(evento), INICIALIZANDO);
        /* TODO: esta operación de inicialización es muy pesada. Capaz estaría bueno activar en Redis la característica
            de snapshots, aof, o algún otro tipo de backup a disco, y después, en esta inicialización, hacer un peek
            a la última inscripción en la cola Redis, y solamente insertar las posteriores a esa */
        /* Busca a todos los pendientes y los inserta en la waitlist en el orden correspondiente */
        List<InscripcionEvento> inscripcionesPendientes = inscripcionesRepository
                .findByEventoAndEstadoOrderByFechaHoraIngresoAWaitlist(evento, EstadoInscripcion.PENDIENTE);
        agregarAWaitlist(inscripcionesPendientes, waitlistAInicializar);

        var ultimaAgregada = inscripcionesPendientes.get(inscripcionesPendientes.size() - 1);
        /* Mientras estábamos agregando, se guardaron nuevas inscripciones en la base. Agrego esas. */
        List<InscripcionEvento> nuevasInscripcionesPendientes = inscripcionesRepository
                .pendientesPosterioresALaFechaOrdenados(evento,
                        ultimaAgregada.getFechaHoraIngresoAWaitlist().orElse(null));
        agregarAWaitlist(nuevasInscripcionesPendientes, waitlistAInicializar);

        /* Marca como inicializado */
        flagsInicializacion.setEstadoInicializacion(flagInicializacionCola(evento), LISTO);
    }

    EstadoInicializacionRedis estadoInicializacionWaitlist(Evento evento) {
        return flagsInicializacion.getEstadoInicializacion(flagInicializacionCola(evento));
    }

    private void agregarAWaitlist(List<InscripcionEvento> inscripciones, Waitlist waitlist) {
        inscripciones.stream().forEach(i -> waitlist.agregar(i.getId()));
    }

    private static String flagInicializacionCola(Evento e) {
        return "estado-inicializacion-cola:" + e.getId();
    }
}
