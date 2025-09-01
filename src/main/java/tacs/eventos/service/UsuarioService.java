package tacs.eventos.service;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tacs.eventos.dto.EstadoInscripcionResponse;
import tacs.eventos.dto.InscripcionResponse;
import tacs.eventos.model.Evento;
import tacs.eventos.model.RolUsuario;
import tacs.eventos.model.Usuario;
import tacs.eventos.model.inscripcion.EstadoInscripcion;
import tacs.eventos.model.inscripcion.InscripcionEvento;
import tacs.eventos.repository.WaitlistRepository;
import tacs.eventos.repository.inscripcion.InscripcionesRepository;
import tacs.eventos.repository.usuario.UsuarioRepository;

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

    public Optional<Usuario> buscarPorId(String id) {
        return repo.obtenerPorId(id);
    }

    public List<InscripcionResponse> obtenerInscripciones(String usuarioId) {
        Usuario usuario = repo.obtenerPorId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        List<InscripcionEvento> inscripciones = inscripcionesRepository.getInscripcionesPorParticipante(usuario);
        List<InscripcionResponse> inscripcionResponses = inscripciones.stream()
                .map(insc -> new InscripcionResponse(insc.getEvento().getId(), mapEstado(insc.getEstado())))
                .collect(Collectors.toList());

        List<Evento> eventosEnWaitlist = waitlistRepository.eventosEnCuyasWaitlistEsta(usuario);
        List<InscripcionResponse> waitlistResponses = eventosEnWaitlist.stream()
                .map(evento -> new InscripcionResponse(evento.getId(), EstadoInscripcionResponse.EN_WAITLIST))
                .collect(Collectors.toList());

        return Stream.concat(inscripcionResponses.stream(), waitlistResponses.stream()).collect(Collectors.toList());
    }

    private EstadoInscripcionResponse mapEstado(EstadoInscripcion estado) {
        return switch (estado) {
        case CONFIRMADA -> EstadoInscripcionResponse.CONFIRMADA;
        case CANCELADA -> EstadoInscripcionResponse.CANCELADA;
        case WAITLIST -> EstadoInscripcionResponse.EN_WAITLIST;
        };
    }
}
