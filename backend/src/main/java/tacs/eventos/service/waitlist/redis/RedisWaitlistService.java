package tacs.eventos.service.waitlist.redis;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import tacs.eventos.model.Evento;
import tacs.eventos.model.inscripcion.EstadoInscripcion;
import tacs.eventos.model.inscripcion.InscripcionEvento;
import tacs.eventos.model.waitlist.Waitlist;
import tacs.eventos.model.waitlist.WaitlistRedis;
import tacs.eventos.model.waitlist.WaitlistRedisTemporal;
import tacs.eventos.repository.inscripcion.InscripcionesRepository;
import tacs.eventos.service.WaitlistService;
import tacs.redis_utils.FlagsInicializacionRedis;
import tacs.redis_utils.InicializadorRedis;

import java.util.Optional;

import static tacs.redis_utils.EstadoInicializacionRedis.*;

@Service
@RequiredArgsConstructor
public class RedisWaitlistService implements WaitlistService, InicializadorRedis {
    private final InscripcionesRepository inscripcionesRepository;
    private RedissonClient redisson;
    private FlagsInicializacionRedis flagsInicializacion;

    @Override
    public WaitlistRedis waitlist(Evento evento) {
        /* No hay necesidad de guardarse las waitlist en memoria o en otro repositorio, porque cada
        RedisWaitlist accede directamente a redis para ver la cola para ese evento.
         */
        return flagsInicializacion.getEstadoInicializacion(flagInicializacionCola(evento)) == LISTO
                ? watilistPermanente(evento)
                : waitlistTemporal(evento);
    }

    private @NonNull WaitlistRedis watilistPermanente(Evento evento) {
        return new WaitlistRedis(evento, redisson, "WAITLIST_EVENTO_");
    }

    private @NonNull WaitlistRedisTemporal waitlistTemporal(Evento evento) {
        return new WaitlistRedisTemporal(evento, redisson, "WAITLIST_TEMPORAL_EVENTO_", inscripcionesRepository);
    }

    @Override
    public void inicializar() {
        inscripcionesRepository.findDistinctEvento().filter(e ->
                        flagsInicializacion.getEstadoInicializacion(flagInicializacionCola(e)) == NO_INICIALIZADO)
                .forEach(this::inicializarCola);
    }

    private void inicializarCola(Evento evento) {
        /* Avisa que está inicializando esta cola, para que ninguna otra instancia de este servicio intente hacerlo */
        flagsInicializacion.setEstadoInicializacion(flagInicializacionCola(evento), INICIALIZANDO);
        /* Busca a todos los pendientes y los inserta en la waitlist permanente en el orden correspondiente */
        Waitlist waitlistPermantente = watilistPermanente(evento);
        inscripcionesRepository.findByEventoAndEstadoOrderByfechaHoraIngresoAWaitlist(evento, EstadoInscripcion.PENDIENTE)
                .stream().map(InscripcionEvento::getId)
                .forEach(waitlistPermantente::agregar);
        /* Mientras se inicializaba, se estuvieron insertando inscripciones en la cola temporal. Vacía esa cola, poniendo las inscripciones en la cola permanente */
        Waitlist waitlistTemporal = waitlistTemporal(evento);
        Optional<String> idInscripcion;
        while ((idInscripcion = waitlistTemporal.proximo()).isPresent())
            waitlistPermantente.agregar(idInscripcion.get());
        /* Marca como inicializado */
        flagsInicializacion.setEstadoInicializacion(flagInicializacionCola(evento), LISTO);
    }

    private static String flagInicializacionCola(Evento e) {
        return RedisWaitlistService.class + "_COLA_" + e.getId();
    }
}
