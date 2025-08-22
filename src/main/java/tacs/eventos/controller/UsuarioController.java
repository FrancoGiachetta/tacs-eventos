package tacs.eventos.controller;

import org.springframework.web.bind.annotation.*;
import tacs.eventos.dto.EventoDTO;
import tacs.eventos.service.UsuarioService;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/mis-inscripciones/{userId}")
    public List<EventoDTO> getMisInscripciones(
            @PathVariable String userId) {
        return usuarioService.obtenerInscripciones(userId);
    }
}
