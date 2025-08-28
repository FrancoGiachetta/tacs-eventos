package tacs.eventos.controller;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;
import tacs.eventos.dto.EventoDTO;
import tacs.eventos.model.Evento;
import tacs.eventos.service.EventoService;

import java.util.List;

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
    public List<Evento> listarEventos() {
        return eventoService.listarEventos();
    }
}
