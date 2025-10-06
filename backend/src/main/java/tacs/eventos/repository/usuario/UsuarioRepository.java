package tacs.eventos.repository.usuario;

import org.springframework.data.mongodb.repository.MongoRepository;
import tacs.eventos.model.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {
    /**
     * @return todos los usuarios.
     */
    // List<Usuario> obtenerTodos();

    /**
     * @param email
     *            email de un usuario.
     *
     * @return el usuario al que le corresponde el email.
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * @param id
     *            id de un usuario.
     *
     * @return el usuario al que le corresponde el id.
     */
    // Optional<Usuario> obtenerPorId(String id);

    /**
     * Guarda un usuario.
     *
     * @param usuario
     *            usuario a guardar.
     */
    // void guardar(Usuario usuario);

    /**
     * Elimina un usuario.
     *
     * @param usuario
     *            usuario a eliminar.
     */
    // void eliminar(Usuario usuario);
}
