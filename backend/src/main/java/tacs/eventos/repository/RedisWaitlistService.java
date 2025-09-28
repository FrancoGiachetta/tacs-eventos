package tacs.eventos.repository;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import tacs.eventos.model.Evento;
import tacs.eventos.model.waitlist.RedisWaitlist;

@Service
@RequiredArgsConstructor
public class RedisWaitlistService implements WaitlistService {
    private RedissonClient redisson;

    @Override
    public RedisWaitlist waitlist(Evento evento) {
        /* No hay necesidad de guardarse las waitlist en memoria o en otro repositorio, porque cada
        RedisWaitlist accede directamente a redis para ver la cola para ese evento.
         */
        return new RedisWaitlist(evento, redisson);
    }
}
