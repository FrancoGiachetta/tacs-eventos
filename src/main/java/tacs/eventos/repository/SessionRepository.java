package tacs.eventos.repository;

import tacs.eventos.model.Session;

import java.util.Optional;

public interface SessionRepository {
    Session save(Session s);

    Optional<Session> findByToken(String token);

    void invalidate(String token);
}
