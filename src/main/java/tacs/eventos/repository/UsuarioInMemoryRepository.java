package tacs.eventos.repository;

import org.springframework.stereotype.Repository;
import tacs.eventos.model.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UsuarioInMemoryRepository implements UsuarioRepository {
    // thread-safe
    // mail como key TODO: temporalmente en memoria pasar a NO SQL || ver si conviene el mail como key o la id
    private final Map<String, Usuario> usuarios = new ConcurrentHashMap<>();

    @Override
    public List<Usuario> obtenerTodos() {
        return new ArrayList<>(usuarios.values());
    }

    @Override
    public Optional<Usuario> obtenerPorEmail(String email) {
        if (email == null)
            return Optional.empty();
        return Optional.ofNullable(usuarios.get(email.toLowerCase()));
    }

    @Override
    public Optional<Usuario> obtenerPorId(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return usuarios.values().stream().filter(usuario -> usuario.getId() != null && usuario.getId().equals(id))
                .findFirst();
    }

    @Override
    public void guardar(Usuario usuario) {
        usuarios.put(usuario.getEmail().toLowerCase(), usuario);
    }

    @Override
    public void eliminar(Usuario usuario) {
        if (usuario != null && usuario.getEmail() != null) {
            usuarios.remove(usuario.getEmail().toLowerCase());
        }
    }
}
