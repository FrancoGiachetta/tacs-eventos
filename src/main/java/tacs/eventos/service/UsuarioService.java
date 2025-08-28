package tacs.eventos.service;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tacs.eventos.model.Evento;
import tacs.eventos.model.RolUsuario;
import tacs.eventos.model.Usuario;
import tacs.eventos.model.inscripcion.InscripcionEvento;
import tacs.eventos.repository.InscripcionesRepository;
import tacs.eventos.repository.UsuarioRepository;
import tacs.eventos.repository.WaitlistRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class UsuarioService {
    private final UsuarioRepository repo;
    private final InscripcionesRepository inscripcionesRepository;
    private final WaitlistRepository waitlistRepository;
    private final PasswordEncoder encoder;

    public Usuario registrar(String email, String password) {
        Optional<Usuario> existente = repo.obtenerPorEmail(email);
        if (existente.isPresent())
            throw new IllegalArgumentException("Email ya registrado");
        var u = new Usuario(email, encoder.encode(password), Set.of(RolUsuario.USUARIO));
        repo.guardar(u);
        return u;
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return repo.obtenerPorEmail(email);
    }

    public List<Evento> obtenerInscripciones(String usuarioId) {
        Usuario usuario = repo.obtenerPorId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        List<InscripcionEvento> inscripciones = inscripcionesRepository.getInscripcionesPorParticipante(usuario);
        List<Evento> eventosConInscripcionConfirmada = inscripciones.stream().map(InscripcionEvento::getEvento)
                .collect(Collectors.toList());
        List<Evento> eventosEnWaitlist = waitlistRepository.eventosEnCuyasWaitlistEsta(usuario);
        return Stream.concat(eventosEnWaitlist.stream(), eventosConInscripcionConfirmada.stream())
                .collect(Collectors.toList());
    }
}
