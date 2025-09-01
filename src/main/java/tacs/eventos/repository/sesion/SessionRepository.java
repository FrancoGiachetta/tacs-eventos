package tacs.eventos.repository.sesion;

import tacs.eventos.model.Session;

import java.util.Optional;

public interface SessionRepository {
    /**
     *  Guarda una nueva sesion.
     *
     * @param session una sesión.
     *
     * @return la sesion guardada.
     */
    Session save(Session session);

    /**
     * @param token token asosiado a una sesión.
     *
     * @return la sesión asociada al token, vacío si no existe tal sesión.
     */
    Optional<Session> findByToken(String token);

    void invalidate(String token);
}
