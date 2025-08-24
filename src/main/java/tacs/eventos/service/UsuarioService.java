package tacs.eventos.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tacs.eventos.dto.EventoDTO;
import tacs.eventos.model.InscripcionEvento;
import tacs.eventos.model.RolUsuario;
import tacs.eventos.model.Usuario;
import tacs.eventos.repository.inscripcion.InscripcionesRepository;
import tacs.eventos.repository.usuario.UsuarioRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UsuarioService {
    private final UsuarioRepository repo;
    private final InscripcionesRepository inscripcionesRepository;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository repo, InscripcionesRepository repoInscripciones, PasswordEncoder encoder) {
        this.repo = repo;
        this.inscripcionesRepository = repoInscripciones;
        this.encoder = encoder;
    }

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

    public List<EventoDTO> obtenerInscripciones(String usuarioId) {
        List<InscripcionEvento> inscripciones = inscripcionesRepository.getInscripcionesPorParticipante(usuarioId);
        return inscripciones.stream().map(InscripcionEvento::getEvento).map(EventoDTO::fromEntity).toList();
    }
}
