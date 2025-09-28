package tacs.eventos.model.waitlist;

import org.redisson.api.RedissonClient;
import tacs.eventos.model.Evento;

import java.util.Optional;
import java.util.Queue;

/**
 * Waitlist que guarda la cola como una Queue de Redis.
 */
public class WaitlistRedis implements Waitlist {
    protected final RedissonClient redisson;
    protected final Evento evento;
    protected final Queue<String> items;

    public WaitlistRedis(Evento evento, RedissonClient redisson, String prefijoIdDeCola) {
        this.redisson = redisson;
        this.evento = evento;
        this.items = redisson.getQueue(prefijoIdDeCola + evento.getId());
    }

    public void agregar(String idInscripcion) {
        items.add(idInscripcion);
    }

    public Optional<String> proximo() {
        return Optional.ofNullable(items.poll());
    }
}
