package tacs.eventos.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.modelmapper.ModelMapper;
import tacs.eventos.dto.EventoDTO;
import tacs.eventos.dto.FiltrosEventoDTO;
import tacs.eventos.dto.InscripcionDTO;
import tacs.eventos.model.Evento;
import tacs.eventos.repository.FiltroBusqueda;
import tacs.eventos.repository.evento.busqueda.FiltradoPorCategoria;
import tacs.eventos.repository.evento.busqueda.FiltradoPorFechaInicio;
import tacs.eventos.repository.evento.busqueda.FiltradoPorPalabrasClave;
import tacs.eventos.repository.evento.busqueda.FiltradoPorPrecio;
import tacs.eventos.service.EventoService;

import java.time.LocalDate;
import java.util.ArrayList;
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
    public List<Evento> listarEventos(
            @RequestParam(required = false) Double precioMinimoParam,
            @RequestParam(required = false) Double precioMaximoParam,
            @RequestParam(required = false) LocalDate fechaMinParam,
            @RequestParam(required = false) LocalDate fechaMaxParam,
            @RequestParam(required = false) String categoriaParam,
            @RequestParam(required = false) List<String> palabrasClaveParam
    ) {
        if (precioMinimoParam == null && precioMaximoParam == null && fechaMinParam == null && fechaMaxParam == null && categoriaParam == null && palabrasClaveParam == null) {
            return eventoService.listarEventos();
        } else {
            LocalDate fechaMinima = fechaMinParam != null ? fechaMinParam : LocalDate.now();
            LocalDate fechaMaxima = fechaMaxParam != null ? fechaMaxParam : LocalDate.MAX;
            Double precioMinimoPesos = precioMinimoParam != null ? precioMinimoParam : 0.0;
            Double precioMaximoPesos = precioMaximoParam != null ? precioMaximoParam : Double.MAX_VALUE;

            List<FiltroBusqueda<Evento>> filtros = new ArrayList<>();

            filtros.add(new FiltradoPorFechaInicio(fechaMinima, fechaMaxima));
            filtros.add(new FiltradoPorPrecio(precioMinimoPesos, precioMaximoPesos));

            if (categoriaParam != null) {
                filtros.add(new FiltradoPorCategoria(categoriaParam));
            }

            if (palabrasClaveParam != null) {
                filtros.add(new FiltradoPorPalabrasClave(palabrasClaveParam));
            }

            return  eventoService.filtrarEventos(filtros);
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