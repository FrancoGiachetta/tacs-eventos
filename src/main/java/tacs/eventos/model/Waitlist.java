package tacs.eventos.model;

import lombok.Getter;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class Waitlist {
    @Getter
    private final Evento evento;
    private final ConcurrentLinkedQueue<InscripcionEnWaitlist> items = new ConcurrentLinkedQueue<>();
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
     * @param candidato
     */
    public void agregar(Usuario candidato) {
        // try {
        items.offer(new InscripcionEnWaitlist(candidato));
        // } catch (IllegalStateException excepcionPorColaLlena) {
        /*
         * TODO: esto puede pasar en otras implementaciones de colas (no en esta). Si dejamos esta, simplemente crece
         * hasta dejar el Java sin memoria. Tendríamos que solucionar ese problema.
         */
        // throw new ColaLlenaException();
        // }
    }

    /**
     * Anula la suscripción de este candidato a la waitlist de este evento. Puede ocurrir que justo se estuviera
     * aceptando la suscripción en ese momento. Si se da el caso, la suscripción va a haber quedado confirmada en lugar
     * de anulada.
     *
     * @param candidato
     */
    public void anularInscripcion(Usuario candidato) {
        items.removeIf(item -> item.getCandidato().equals(candidato));
    }

    /**
     * Saca y devuelve el próximo usuario en la waitlist, o un Optional vacío si la waitlist está vacía.
     */
    public Optional<InscripcionEnWaitlist> proximo() {
        return Optional.ofNullable(items.poll());
    }

    /**
     * @param candidato
     *
     * @return si esta waitlist contiene a ese candidato
     */
    public boolean contiene(Usuario candidato) {
        return candidatos().stream().anyMatch(candidato::equals);
    }

    /**
     * @return todos los candidatos que forman parte de esta waitlist, en orden
     */
    public List<Usuario> candidatos() {
        return items.stream().map(InscripcionEnWaitlist::getCandidato).collect(Collectors.toList());
    }
}
