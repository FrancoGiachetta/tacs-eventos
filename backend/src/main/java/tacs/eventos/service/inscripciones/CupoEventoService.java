package tacs.eventos.service.inscripciones;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import tacs.eventos.model.evento.Evento;
import tacs.eventos.model.inscripcion.EstadoInscripcion;
import tacs.eventos.redis_utils.EstadoInicializacionRedis;
import tacs.eventos.redis_utils.FlagsInicializacionRedis;
import tacs.eventos.repository.inscripcion.InscripcionesRepository;

@Service
@RequiredArgsConstructor
public class CupoEventoService {
    private final RedissonClient redisson;
    private final InscripcionesRepository inscripcionesRepository;
    private final FlagsInicializacionRedis inicializacionRedis;

    /*
     * TODO: manejar caso de que el organizador del evento modifique el cupo máximo del evento. Tendríamos que volver a
     * inicializar el semáforo, o invalidarlo seteándole el estado NO_INICIALIZADO
     */

    /**
     * Intenta obtener un cupo para el evento. Si no hay cupo, retorna false. Si hay cupo, reserva uno y retorna true.
     *
     * @param evento
     * @return si se logró obtener un cupo
     */
    public boolean obtenerCupo(Evento evento) {
        var semaforo = getSemaforoCupos(evento);
        if (semaforo.tryAcquire()) // Intenta obtener un cupo
            return true; // Si pudo, retorna que hay un cupo
        // Si no pudo, puede ser que sea porque el semáforo no está inicializado. Si es el caso, lo inicializa
        var estadoSemaforo = estadoInicializacionSemaforo(evento);
        if (estadoSemaforo == EstadoInicializacionRedis.LISTO) // Si sí estaba inicializado
            return false; // Entonces realmente no había cupo. Retorna False.
        else { // Si no estaba inicializado
            inicializarOEsperarQueSeInicialiceElSemaforo(evento, estadoSemaforo); // Lo deja inicializado
            return semaforo.tryAcquire(); // Intenta obtener un cupo, ahora con el semáforo inicializado
        }
    }

    public void devolverCupo(Evento evento) {
        getSemaforoCupos(evento).release();
    }

    private void inicializarOEsperarQueSeInicialiceElSemaforo(Evento evento, EstadoInicializacionRedis estadoSemaforo) {
        if (estadoSemaforo == EstadoInicializacionRedis.NO_INICIALIZADO)
            inicializarSemaforo(evento);
        else if (estadoSemaforo == EstadoInicializacionRedis.INICIALIZANDO) {
            // Si se está inicializando, espero a que termine
            try {
                sincronizacionInicializacionSemaforo(evento).await();
            } catch (InterruptedException e) {
                /*
                 * TODO: ver cómo hacemos el logueo (recordar que tenemos muchas instancias, y encima usamos docker, así
                 * que es inviable escribir en un archivo)
                 */
                e.printStackTrace();
                inicializarSemaforo(evento); // Si hubo un error, lo hago yo
            }
        }
    }

    private void inicializarSemaforo(Evento evento) {
        // Aviso que estoy inicializando
        inicializacionRedis.setEstadoInicializacion(keyEstadoSemaforo(evento), EstadoInicializacionRedis.INICIALIZANDO);
        RCountDownLatch latch = sincronizacionInicializacionSemaforo(evento);
        latch.trySetCount(1); // Bloqueo para que otros hilos esperen a que termine de inicializar
        try {
            RSemaphore semaforo = getSemaforoCupos(evento);
            int inscriptos = this.inscripcionesRepository.countByEventoAndEstado(evento, EstadoInscripcion.CONFIRMADA);
            semaforo.trySetPermits(evento.getCupoMaximo() - inscriptos);
        } finally {
            latch.countDown(); // Desbloquea para que otros hilos sepan que ya inicialicé
        }
        // Aviso que quedó inicializado
        inicializacionRedis.setEstadoInicializacion(keyEstadoSemaforo(evento), EstadoInicializacionRedis.LISTO);
    }

    private EstadoInicializacionRedis estadoInicializacionSemaforo(Evento evento) {
        return inicializacionRedis.getEstadoInicializacion(keyEstadoSemaforo(evento));
    }

    private String keyEstadoSemaforo(Evento evento) {
        return "estado-inicializacion-cupos:" + evento.getId();
    }

    private RSemaphore getSemaforoCupos(Evento evento) {
        return redisson.getSemaphore("cupos-evento:" + evento.getId());
    }

    private RCountDownLatch sincronizacionInicializacionSemaforo(Evento evento) {
        return redisson.getCountDownLatch("sincronizacion-init-cupos-evento:" + evento.getId());
    }

}
