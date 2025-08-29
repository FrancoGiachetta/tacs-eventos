package tacs.eventos.controller;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;
import tacs.eventos.dto.EventoDTO;
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

@RestController
@RequestMapping("/api/v1/eventos")
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
            @RequestParam(value = "precioPesosMin", required = false) Double precioMinimoParam,
            @RequestParam(value = "precioPesosMax", required = false) Double precioMaximoParam,
            @RequestParam(value = "fechaInicioMin", required = false) LocalDate fechaMinParam,
            @RequestParam(value = "fechaInicioMax", required = false) LocalDate fechaMaxParam,
            @RequestParam(value = "categoria", required = false) String categoriaParam,
            @RequestParam(value = "palabrasClave", required = false) List<String> palabrasClaveParam) {
        if (precioMinimoParam == null && precioMaximoParam == null && fechaMinParam == null && fechaMaxParam == null
                && categoriaParam == null && palabrasClaveParam == null) {
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

            return eventoService.filtrarEventos(filtros);
        }
    }
}
