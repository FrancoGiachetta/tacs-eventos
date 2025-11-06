package tacs.eventos.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tacs.eventos.dto.InscripcionResponse;
import tacs.eventos.model.RolUsuario;
import tacs.eventos.model.Usuario;
import tacs.eventos.model.inscripcion.InscripcionEvento;
import tacs.eventos.repository.inscripcion.InscripcionesRepository;
import tacs.eventos.repository.usuario.UsuarioRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository repo;
    private final InscripcionesRepository inscripcionesRepository;
    private final PasswordEncoder encoder;

    @PostConstruct
    public void inicializarUsuariosIniciales() {
        // Crear admin por defecto si no existe
        if (repo.findByEmail("admin@eventos.com").isEmpty()) {
            Usuario admin = new Usuario("admin@eventos.com", encoder.encode("admin123"), Set.of(RolUsuario.ADMIN));
            repo.save(admin);
            System.out.println("Admin creado: admin@eventos.com / admin123");
        }

        // Crear organizador de ejemplo si no existe
        if (repo.findByEmail("organizador@eventos.com").isEmpty()) {
            Usuario organizador = new Usuario("organizador@eventos.com", encoder.encode("org123"),
                    Set.of(RolUsuario.ORGANIZADOR));
            repo.save(organizador);
            System.out.println("Organizador creado: organizador@eventos.com / org123");
        }
    }

    public Usuario registrar(String email, String password) {
        return registrar(email, password, "USUARIO");
    }

    public Usuario registrar(String email, String password, String tipoUsuario) {
        Optional<Usuario> existente = repo.findByEmail(email);
        if (existente.isPresent())
            throw new IllegalArgumentException("Email ya registrado");

        // Determinar rol basado en tipo de usuario seleccionado
        Set<RolUsuario> roles = determinarRoles(tipoUsuario);

        var u = new Usuario(email, encoder.encode(password), roles);
        repo.save(u);
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
        return repo.findByEmail(email);
    }

    public Optional<Usuario> buscarPorId(String id) {
        return repo.findById(id);
    }

    public List<InscripcionResponse> obtenerInscripcionesNoCanceladas(String usuarioId) {
        Usuario usuario = repo.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        List<InscripcionEvento> inscripciones = inscripcionesRepository.noCanceladasDeParticipante(usuario);
        List<InscripcionResponse> inscripcionResponses = inscripciones.stream()
                .map(InscripcionResponse::fromInscripcion).toList();
        return inscripcionResponses;
    }

    // Métodos para gestión de roles (solo admin)
    public List<Usuario> obtenerTodosLosUsuarios() {
        return repo.findAll();
    }

    public Usuario cambiarRol(String usuarioId, RolUsuario nuevoRol) {
        Usuario usuario = repo.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        usuario.setRoles(Set.of(nuevoRol));
        repo.save(usuario);
        System.out.println("Rol cambiado para usuario " + usuario.getEmail() + " a: " + nuevoRol);
        return usuario;
    }
}