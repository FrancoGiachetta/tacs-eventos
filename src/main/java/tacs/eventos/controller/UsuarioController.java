package tacs.eventos.controller;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tacs.eventos.dto.InscripcionEventoDTO;
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

    @GetMapping("/usuarios/{email}")
    public Optional<Usuario> getUsuario(@PathVariable String email) {
        return usuarioService.buscarPorEmail(email);
    }

    @GetMapping("/mis-inscripciones/{userId}")
    public List<InscripcionEventoDTO> getMisInscripciones(@PathVariable String userId) {
        return usuarioService.obtenerInscripciones(userId);
    }
}
