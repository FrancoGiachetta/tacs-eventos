package tacs.eventos.model.waitlist;

import lombok.Getter;
import org.redisson.api.RedissonClient;
import tacs.eventos.model.Evento;

import java.util.Optional;
import java.util.Queue;

public class RedisWaitlist implements Waitlist {
    private final RedissonClient redisson;

    @Getter
    private final Evento evento;
    private final Queue<String> items;

    public RedisWaitlist(Evento evento, RedissonClient redisson) {
        this.redisson = redisson;
        this.evento = evento;
        this.items = redisson.getQueue(idDeCola(evento));
    }

    public void agregar(String idInscripcion) {
        items.add(idInscripcion);
    }

    public Optional<String> proximo() {
        return Optional.ofNullable(items.poll());
    }

    private static String idDeCola(Evento idEvento) {
        return "WAITLIST_EVENTO_" + idEvento; // OBVIAMENTE, NO CAMBIAR ESTE STRING, PORQUE ROMPE LOS DATOS DE REDIS
    }
}
