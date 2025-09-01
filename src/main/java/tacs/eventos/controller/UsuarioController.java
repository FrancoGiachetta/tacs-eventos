package tacs.eventos.controller;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tacs.eventos.dto.InscripcionEventoDTO;
import tacs.eventos.model.Evento;
import tacs.eventos.model.Usuario;
import tacs.eventos.repository.evento.EventosRepository;
import tacs.eventos.service.UsuarioService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/usuario")
@AllArgsConstructor
public class UsuarioController
{

    private final UsuarioService usuarioService;
    private final EventosRepository eventosRepository;

    @GetMapping("/{email}") // todo eliminar este endpoint
    public Optional<Usuario> getUsuario(@PathVariable String email)
    {
        return usuarioService.buscarPorEmail(email);
    }

    @GetMapping("/mis-inscripciones")
    public List<InscripcionEventoDTO> getMisInscripciones(@AuthenticationPrincipal Usuario usuario)
    {
        return usuarioService.obtenerInscripciones(usuario.getId());
    }

    @GetMapping("/mis-eventos")
    public List<Evento> getMisEventos(@AuthenticationPrincipal Usuario usuario)
    {
        return eventosRepository.getEventosPorOrganizador(usuario.getId());
    }
}
