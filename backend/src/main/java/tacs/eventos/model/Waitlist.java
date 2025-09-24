package tacs.eventos.model;

import lombok.Getter;
import tacs.eventos.model.evento.Evento;
import tacs.eventos.model.inscripcion.InscripcionEnWaitlist;

import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Waitlist {
    @Getter
    private final Evento evento;
    private final ConcurrentLinkedQueue<String> items = new ConcurrentLinkedQueue<>();
    /*
     * Cola que crece dinámicamente, y permite acceso simultáneo desde múltiples hilos. Esto va a ser útil para cuando
     * tengamos varias inscripciones entrando y saliendo a la vez de la waitlist. // TODO: cuando llegue la hora de
     * optimizar para el rendimiento, ver si hay una mejor opción googleando "Java concurrent queue" o algo así.
     */

    /**
     * Crea una waitlist para este evento.
     *
     * @param evento
     */
    public Waitlist(Evento evento) {
        this.evento = evento;
    }

    /**
     * Agrega este candidato a la waitlist de este evento.
     *
     * @param idInscripcion
     */
    public void agregar(String idInscripcion) {
        // try {
        items.offer(idInscripcion);
        // } catch (IllegalStateException excepcionPorColaLlena) {
        /*
         * TODO: esto puede pasar en otras implementaciones de colas (no en esta). Si dejamos esta, simplemente crece
         * hasta dejar el Java sin memoria. Tendríamos que solucionar ese problema.
         */
        // throw new ColaLlenaException();
        // }
    }

    /**
     * Saca y devuelve el próximo usuario en la waitlist, o un Optional vacío si la waitlist está vacía.
     */
    public Optional<String> proximo() {
        return Optional.ofNullable(items.poll());
    }

    public int cantidadEnCola() {
        return items.size();
    }
}
