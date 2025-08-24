package tacs.eventos.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.modelmapper.ModelMapper;
import tacs.eventos.dto.EventoDTO;
import tacs.eventos.dto.FiltrosEventoDTO;
import tacs.eventos.dto.InscripcionDTO;
import tacs.eventos.model.Evento;
import tacs.eventos.repository.FiltroBusqueda;
import tacs.eventos.service.EventoService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/eventos")
@AllArgsConstructor
public class EventoController {

    private final EventoService eventoService;

    private final ModelMapper mapper;

    @PostMapping
    public Evento crearEvento(@RequestBody EventoDTO dto) {
        Evento evento = mapper.map(dto, Evento.class);
        return eventoService.crearEvento(evento);
    }

    @GetMapping
    public List<Evento> listarEventos(@RequestParam Optional<FiltrosEventoDTO> filtroParams) {
        if (filtroParams.isPresent()) {
            List<FiltroBusqueda<Evento>> filtros = filtroParams.get().toListFiltroBusqueda();

            return eventoService.filtrarEventos(filtros);
        } else {
            return eventoService.listarEventos();
        }
    }

    @PostMapping("/{eventoId}/inscripcion")
    public String inscribirUsuario(@PathVariable String eventoId, @RequestBody InscripcionDTO dto) {
        boolean confirmado = eventoService.inscribirUsuario(eventoId, dto.usuarioId());
        return confirmado ? "Usuario inscrito correctamente" : "Usuario agregado a waitlist";
    }

    @PostMapping("/{eventoId}/cancelar")
    public String cancelarInscripcion(@PathVariable String eventoId, @RequestBody InscripcionDTO dto) {
        boolean exito = eventoService.cancelarInscripcion(eventoId, dto.usuarioId());
        return exito ? "CancelaciÃ³n realizada" : "Usuario no encontrado";
    }
}