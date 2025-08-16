package tacs.eventos.controller;

import org.springframework.web.bind.annotation.*;
import tacs.eventos.dto.EventoDTO;
import tacs.eventos.dto.InscripcionDTO;
import tacs.eventos.model.Evento;
import tacs.eventos.service.EventoService;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @PostMapping
    public Evento crearEvento(@RequestBody EventoDTO dto) {
        Evento evento = new Evento(dto.titulo, dto.descripcion, dto.fechaHoraInicio,
                dto.duracionMinutos, dto.ubicacion, dto.cupoMaximo,
                dto.precio, dto.categoria);
        return eventoService.crearEvento(evento);
    }

    @GetMapping
    public List<Evento> listarEventos() {
        return eventoService.listarEventos();
    }

    @PostMapping("/{eventoId}/inscripcion")
    public String inscribirUsuario(@PathVariable String eventoId, @RequestBody InscripcionDTO dto) {
        boolean confirmado = eventoService.inscribirUsuario(eventoId, dto.usuarioId);
        return confirmado ? "Usuario inscrito correctamente" : "Usuario agregado a waitlist";
    }

    @PostMapping("/{eventoId}/cancelar")
    public String cancelarInscripcion(@PathVariable String eventoId, @RequestBody InscripcionDTO dto) {
        boolean exito = eventoService.cancelarInscripcion(eventoId, dto.usuarioId);
        return exito ? "Cancelación realizada" : "Usuario no encontrado";
    }
}
