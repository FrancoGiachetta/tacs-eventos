package tacs.eventos.repository;

import org.springframework.stereotype.Repository;
import tacs.eventos.model.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class RepositorioUsuarios
{
  // thread-safe
  // mail como key TODO: temporalmente en memoria pasar a NO SQL
  private final Map<String, Usuario> usuarios = new ConcurrentHashMap<>();

  public Optional<Usuario> findByEmailIgnoreCase(String email) {
    if (email == null) return Optional.empty();
    return Optional.ofNullable(usuarios.get(email.toLowerCase()));
  }

  public Usuario save(Usuario usuario) {
    usuarios.put(usuario.getEmail().toLowerCase(), usuario);
    return usuario;
  }

  public List<Usuario> findAll() {
    return new ArrayList<>(usuarios.values());
  }
}
