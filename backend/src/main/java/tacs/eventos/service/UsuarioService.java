package tacs.eventos.service;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import tacs.eventos.dto.EstadoInscripcionResponse;
import tacs.eventos.dto.InscripcionResponse;
import tacs.eventos.model.Evento;
import tacs.eventos.model.RolUsuario;
import tacs.eventos.model.Usuario;
import tacs.eventos.model.inscripcion.EstadoInscripcion;
import tacs.eventos.model.inscripcion.InscripcionEvento;
import tacs.eventos.repository.inscripcion.InscripcionesRepository;
import tacs.eventos.repository.WaitlistRepository;
import tacs.eventos.repository.usuario.UsuarioRepository;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository repo;
    private final InscripcionesRepository inscripcionesRepository;
    private final WaitlistRepository waitlistRepository;
    private final PasswordEncoder encoder;

    @PostConstruct
    public void inicializarUsuariosIniciales() {
        // Crear admin por defecto si no existe
        if (repo.obtenerPorEmail("admin@eventos.com").isEmpty()) {
            Usuario admin = new Usuario("admin@eventos.com", encoder.encode("admin123"), Set.of(RolUsuario.ADMIN));
            repo.guardar(admin);
            System.out.println("Admin creado: admin@eventos.com / admin123");
        }

        // Crear organizador de ejemplo si no existe
        if (repo.obtenerPorEmail("organizador@eventos.com").isEmpty()) {
            Usuario organizador = new Usuario("organizador@eventos.com", encoder.encode("org123"),
                    Set.of(RolUsuario.ORGANIZADOR));
            repo.guardar(organizador);
            System.out.println("Organizador creado: organizador@eventos.com / org123");
        }
    }

    public Usuario registrar(String email, String password) {
        return registrar(email, password, "USUARIO");
    }

    public Usuario registrar(String email, String password, String tipoUsuario) {
        Optional<Usuario> existente = repo.obtenerPorEmail(email);
        if (existente.isPresent())
            throw new IllegalArgumentException("Email ya registrado");

        // Determinar rol basado en tipo de usuario seleccionado
        Set<RolUsuario> roles = determinarRoles(tipoUsuario);

        var u = new Usuario(email, encoder.encode(password), roles);
        repo.guardar(u);
        System.out.println("usuario ID: " + u.getId() + " con roles: " + roles);
        return u;
    }

    private Set<RolUsuario> determinarRoles(String tipoUsuario) {
        if (tipoUsuario == null || tipoUsuario.trim().isEmpty()) {
            return Set.of(RolUsuario.USUARIO); // Usuario normal por defecto
        }

        // Solo permitir USUARIO u ORGANIZADOR en registro
        switch (tipoUsuario.toUpperCase()) {
        case "ORGANIZADOR":
            return Set.of(RolUsuario.ORGANIZADOR);
        case "USUARIO":
        default:
            return Set.of(RolUsuario.USUARIO);
        }
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return repo.obtenerPorEmail(email);
    }

    public Optional<Usuario> buscarPorId(String id) {
        return repo.obtenerPorId(id);
    }

    public List<InscripcionResponse> obtenerInscripcionesNoCanceladas(String usuarioId) {
        Usuario usuario = repo.obtenerPorId(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No se pudo encontrar el usuario no encontrado"));
        List<InscripcionEvento> inscripciones = inscripcionesRepository
                .getInscripcionesConfirmadasPorParticipante(usuario);
        List<InscripcionResponse> inscripcionResponses = inscripciones.stream()
                .map(inscripcion -> new InscripcionResponse(inscripcion.getEvento().getId(),
                        mapEstado(inscripcion.getEstado())))
                .collect(Collectors.toList());

        List<Evento> eventosEnWaitlist = waitlistRepository.eventosEnCuyasWaitlistEsta(usuario);
        List<InscripcionResponse> waitlistResponses = eventosEnWaitlist.stream()
                .map(evento -> new InscripcionResponse(evento.getId(), EstadoInscripcionResponse.PENDIENTE)).toList();

        return Stream.concat(inscripcionResponses.stream(), waitlistResponses.stream()).collect(Collectors.toList());
    }

    private EstadoInscripcionResponse mapEstado(EstadoInscripcion estado) {
        return switch (estado) {
        case CONFIRMADA -> EstadoInscripcionResponse.CONFIRMADA;
        case CANCELADA -> EstadoInscripcionResponse.CANCELADA;
        case PENDIENTE -> EstadoInscripcionResponse.PENDIENTE;
        };
    }

    // Métodos para gestión de roles (solo admin)
    public List<Usuario> obtenerTodosLosUsuarios() {
        return repo.obtenerTodos();
    }

    public Usuario cambiarRol(String usuarioId, RolUsuario nuevoRol) {
        Usuario usuario = repo.obtenerPorId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        usuario.setRoles(Set.of(nuevoRol));
        repo.guardar(usuario);
        System.out.println("Rol cambiado para usuario " + usuario.getEmail() + " a: " + nuevoRol);
        return usuario;
    }
}