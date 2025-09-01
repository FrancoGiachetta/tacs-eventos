package tacs.eventos.controller;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tacs.eventos.dto.EventoDTO;
import tacs.eventos.service.UsuarioService;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@AllArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final ModelMapper modelMapper;

    /**
     * Retorna las inscripciones de un usuario según su id.
     *
     * @param userId id del usuario.
     *
     * @return las inscripciones del usuario.
     */
    @GetMapping("/mis-inscripciones/{userId}")
    public List<EventoDTO> getMisInscripciones(@PathVariable String userId) {
        return usuarioService.obtenerInscripciones(userId).stream().map(e -> modelMapper.map(e, EventoDTO.class))
                .toList();
    }
}
