package tacs.eventos.repository;

import org.springframework.stereotype.Repository;
import tacs.eventos.model.Evento;
import tacs.eventos.model.Usuario;
import tacs.eventos.model.Waitlist;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository("waitlistsInMemoryRepo")
public class WaitlistsInMemoryRepo implements WaitlistRepository {
    private Map<Evento, Waitlist> waitlists = new java.util.HashMap<>();

    @Override
    public Waitlist waitlist(Evento evento) {
        return waitlists.computeIfAbsent(evento, Waitlist::new);
    }

    @Override
    public List<Evento> eventosEnCuyasWaitlistEsta(Usuario usuario) {
        return waitlists.entrySet().stream().filter(eventoYWaitlist -> eventoYWaitlist.getValue().contiene(usuario))
                .map(Map.Entry::getKey).collect(Collectors.toList());
    }
}
