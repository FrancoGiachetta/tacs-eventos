package tacs.eventos.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.modelmapper.ModelMapper;
import tacs.eventos.dto.EventoDTO;
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

@RestController
@RequestMapping("/api/eventos")
@AllArgsConstructor
public class EventoController {

    private final EventoService eventoService;

    private final ModelMapper mapper;

    /**
     * Crea un nuevo evento.
     *
     * @param dto datos del evento a crear.
     *
     * @return datos del evento creado.
     */
    @PostMapping
    public Evento crearEvento(@RequestBody EventoDTO dto) {
        Evento evento = mapper.map(dto, Evento.class);
        return eventoService.crearEvento(evento);
    }

    /**
     * Devuelve todos los eventos vigentes. Aplica filtros si los hubiera.
     *
     * @param precioMinimoParam precio mínimo del evento.
     * @param precioMaximoParam precio máximo del evento.
     * @param fechaMinParam fecha mínima de creación del evento.
     * @param fechaMaxParam fecha máxima de creación del evento
     * @param categoriaParam categoría buscada del evento.
     * @param palabrasClaveParam palabras que definen características del evento buscado.
     *
     * @return lista de eventos que cumplan con los filtros utilizados, si los hay.
     */
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

    /**
     * Inscribe un usuario a un evento si éste posee vacantes. De no haber vacantes, la inscripción es colocada
     * en una lista de espera.
     *
     * @param eventoId id del evento al cual inscribir al usuario.
     *
     * @param dto datos de inscripción.
     *
     * @return datos sobre la inscripcion.
     */
    @PostMapping("/{eventoId}/inscripcion")
    public String inscribirUsuario(@PathVariable String eventoId, @RequestBody InscripcionDTO dto) {
        boolean confirmado = eventoService.inscribirUsuario(eventoId, dto.usuarioId());
        return confirmado ? "Usuario inscrito correctamente" : "Usuario agregado a waitlist";
    }

    /**
     * Cancela una inscripción.
     *
     * @param eventoId id del evento del cual cancelar la inscripción.
     *
     * @param dto datos de la inscripción.
     *
     * @return datos sobre la inscripción cancelada.
     */
    @PostMapping("/{eventoId}/cancelar")
    public String cancelarInscripcion(@PathVariable String eventoId, @RequestBody InscripcionDTO dto) {
        boolean exito = eventoService.cancelarInscripcion(eventoId, dto.usuarioId());
        return exito ? "CancelaciÃ³n realizada" : "Usuario no encontrado";
    }
}