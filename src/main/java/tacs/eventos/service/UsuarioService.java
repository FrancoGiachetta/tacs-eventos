package tacs.eventos.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tacs.eventos.model.RolUsuario;
import tacs.eventos.model.Usuario;
import tacs.eventos.repository.UsuarioRepository;

import java.util.Optional;
import java.util.Set;

@Service
public class UsuarioService {
    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
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
}
