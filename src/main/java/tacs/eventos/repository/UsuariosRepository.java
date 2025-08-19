package tacs.eventos.repository;

import tacs.eventos.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuariosRepository
{
  List<Usuario> obtenerTodos();
  Optional<Usuario> obtenerPorEmail(String email);
  Optional<Usuario> obtenerPorId(String id);
  void guardar(Usuario usuario);
  void eliminar(Usuario usuario);
}
