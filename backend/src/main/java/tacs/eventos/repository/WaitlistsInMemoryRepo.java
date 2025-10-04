package tacs.eventos.repository;

import org.springframework.stereotype.Repository;
import tacs.eventos.model.Evento;
import tacs.eventos.model.Waitlist;

import java.util.Map;

@Repository("waitlistsInMemoryRepo")
public class WaitlistsInMemoryRepo implements WaitlistRepository {
    private Map<Evento, Waitlist> waitlists = new java.util.HashMap<>();

    @Override
    public Waitlist waitlist(Evento evento) {
        return waitlists.computeIfAbsent(evento, Waitlist::new);
    }
}
