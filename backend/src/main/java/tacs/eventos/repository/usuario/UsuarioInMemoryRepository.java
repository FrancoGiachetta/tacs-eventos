package tacs.eventos.repository.usuario;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import tacs.eventos.model.RolUsuario;
import tacs.eventos.model.Usuario;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UsuarioInMemoryRepository implements UsuarioRepository {
    // thread-safe
    // mail como key TODO: temporalmente en memoria pasar a NO SQL || ver si conviene el mail como key o la id
    private final Map<String, Usuario> usuarios = new ConcurrentHashMap<>();

    public UsuarioInMemoryRepository(PasswordEncoder pe) {
        // seeder: crea admin si no existe
        Usuario admin = new Usuario("admin@events.local", pe.encode("Admin1234"), Set.of(RolUsuario.ADMIN));
        Usuario organizador = new Usuario("organizador@eventos.com", pe.encode("org123"),
                Set.of(RolUsuario.ORGANIZADOR));
        this.guardar(admin);
        this.guardar(organizador);
        System.out.println("Admin creado: admin@eventos.com / admin123");
        System.out.println("Organizador creado: organizador@eventos.com / org123");
    }

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
