package tacs.eventos.service;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tacs.eventos.dto.InscripcionResponse;
import tacs.eventos.model.RolUsuario;
import tacs.eventos.model.Usuario;
import tacs.eventos.model.inscripcion.InscripcionEvento;
import tacs.eventos.repository.WaitlistRepository;
import tacs.eventos.repository.inscripcion.InscripcionesRepository;
import tacs.eventos.repository.usuario.UsuarioRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static tacs.eventos.dto.EstadoInscripcionMapper.mapEstado;

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
        System.out.println("usuario ID: " + u.getId());
        return u;
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return repo.obtenerPorEmail(email);
    }

    public Optional<Usuario> buscarPorId(String id) {
        return repo.obtenerPorId(id);
    }

    public List<InscripcionResponse> obtenerInscripcionesNoCanceladas(String usuarioId) {
        Usuario usuario = repo.obtenerPorId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        List<InscripcionEvento> inscripciones = inscripcionesRepository.getInscripcionesNoCanceladasPorParticipante(usuario);
        List<InscripcionResponse> inscripcionResponses = inscripciones.stream()
                .map(insc -> new InscripcionResponse(insc.getEvento().getId(), mapEstado(insc.getEstado()))).toList();
        return inscripcionResponses;
    }
}
