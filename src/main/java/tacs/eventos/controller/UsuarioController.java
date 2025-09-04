package tacs.eventos.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tacs.eventos.dto.InscripcionResponse;
import tacs.eventos.model.Evento;
import tacs.eventos.model.Usuario;
import tacs.eventos.repository.evento.EventosRepository;
import tacs.eventos.service.UsuarioService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuario")
@AllArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final EventosRepository eventosRepository;

    /**
     * Retorna las inscripciones de un usuario según su id.
     *
     * @param usuario datos del usuario.
     * @return las inscripciones del usuario.
     */
    @GetMapping("/mis-inscripciones")
    public List<InscripcionResponse> getMisInscripciones(@AuthenticationPrincipal Usuario usuario) {
        return usuarioService.obtenerInscripciones(usuario.getId());
    }

    /**
     * Retorna los eventos para los cuales el usuario es el organizador.
     *
     * @param usuario datos del usuario.
     * @return los eventos organizados por el usuario.
     */
    @GetMapping("/mis-eventos")
    public List<Evento> getMisEventos(@AuthenticationPrincipal Usuario usuario) {
        return eventosRepository.getEventosPorOrganizador(usuario.getId());
    }
}
