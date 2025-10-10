/*
 * package tacs.eventos.repository.sesion;
 *
 * import org.springframework.stereotype.Repository; import tacs.eventos.model.Session;
 *
 * import java.util.Map; import java.util.Optional; import java.util.concurrent.ConcurrentHashMap;
 *
 * @Repository public class SessionInMemoryRepository implements SessionRepository { private final Map<String, Session>
 * byToken = new ConcurrentHashMap<>();
 *
 * @Override public Session save(Session session) { byToken.put(session.getToken(), session); return session; }
 *
 * @Override public Optional<Session> findByToken(String token) { return Optional.ofNullable(byToken.get(token)); }
 *
 * @Override public void invalidate(String token) { byToken.remove(token); } }
 */
