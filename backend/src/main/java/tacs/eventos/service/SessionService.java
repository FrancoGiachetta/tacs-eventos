package tacs.eventos.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tacs.eventos.model.Session;
import tacs.eventos.model.Usuario;
import tacs.eventos.repository.sesion.SessionRepository;
import tacs.eventos.repository.usuario.UsuarioRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
public class SessionService {
    private final UsuarioRepository usuarios;
    private final SessionRepository sesiones;
    private final PasswordEncoder encoder;
    private final int minutes;

    public SessionService(UsuarioRepository usuarios, SessionRepository sesiones, PasswordEncoder encoder,
            @Value("${app.session.minutes:30}") int minutes) {
        this.usuarios = usuarios;
        this.sesiones = sesiones;
        this.encoder = encoder;
        this.minutes = minutes;
    }

    public Optional<Session> login(String email, String rawPassword) {
        return usuarios.findByEmail(email.toLowerCase()).flatMap(u -> encoder.matches(rawPassword, u.getPasswordHash())
                ? Optional.of(createSession(u)) : Optional.empty());
    }

    public void logout(String token) {
        sesiones.findByToken(token).ifPresent(Session::deactivate);
        sesiones.invalidate(token);
    }

    public Optional<Usuario> validate(String token) {
        return sesiones.findByToken(token).filter(s -> s.isActive() && s.getExpiresAt().isAfter(Instant.now()))
                .flatMap(s -> usuarios.findById(s.getUserId()));
    }

    private Session createSession(Usuario u) {
        String token = UUID.randomUUID().toString();
        Instant exp = Instant.now().plus(minutes, ChronoUnit.MINUTES);
        Session s = new Session(token, u.getId(), exp);
        sesiones.save(s);
        return s;
    }

}
