package tacs.eventos.controller;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tacs.eventos.dto.InscripcionResponse;
import tacs.eventos.model.Usuario;
import tacs.eventos.service.UsuarioService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/usuarios")
@AllArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final ModelMapper modelMapper;

    /**
     * Busca un usuario en base a su email.
     *
     * @param email email asociado al usuario a buscar.
     *
     * @return datos del usuario. Vacío si no se encuentra el email.
     */
    @GetMapping("/usuarios/{email}")
    public Optional<Usuario> getUsuario(@PathVariable String email) {
        return usuarioService.buscarPorEmail(email);
    }

    /**
     * Retorna las inscripciones de un usuario según su id.
     *
     * @param userId id del usuario.
     *
     * @return las inscripciones del usuario.
     */
    @GetMapping("/mis-inscripciones/{userId}")
    public List<InscripcionResponse> getMisInscripciones(@PathVariable String userId) {
        return usuarioService.obtenerInscripciones(userId);
    }
}
