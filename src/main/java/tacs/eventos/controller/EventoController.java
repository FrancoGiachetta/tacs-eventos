package tacs.eventos.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tacs.eventos.dto.*;
import tacs.eventos.model.Evento;
import tacs.eventos.model.InscripcionEnWaitlist;
import tacs.eventos.model.Usuario;
import tacs.eventos.model.inscripcion.EstadoInscripcion;
import tacs.eventos.model.inscripcion.InscripcionEvento;
import tacs.eventos.repository.FiltroBusqueda;
import tacs.eventos.repository.evento.busqueda.FiltradoPorCategoria;
import tacs.eventos.repository.evento.busqueda.FiltradoPorFechaInicio;
import tacs.eventos.repository.evento.busqueda.FiltradoPorPalabrasClave;
import tacs.eventos.repository.evento.busqueda.FiltradoPorPrecio;
import tacs.eventos.service.EventoService;
import tacs.eventos.service.InscripcionesService;
import tacs.eventos.service.UsuarioService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/eventos")
@AllArgsConstructor
public class EventoController {

    private final EventoService eventoService;
    private final UsuarioService usuarioService;
    private final InscripcionesService inscripcionesService;
    private final ModelMapper modelMapper;

    private final ModelMapper mapper;

    /**
     * Crea un nuevo evento.
     *
     * @param dto
     *            datos del evento a crear.
     *
     * @return datos del evento creado.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventoDTO crearEvento(@AuthenticationPrincipal String email, @Valid @RequestBody EventoDTO dto) {
        var usuario = this.buscarUsuario(email);

        Evento evento = mapper.map(dto, Evento.class);
        evento.setOrganizador(usuario);

        evento = eventoService.crearEvento(evento);
        return modelMapper.map(evento, EventoDTO.class);
    }

    /**
     * Devuelve todos los eventos vigentes. Aplica filtros si los hubiera.
     *
     * @param precioMinimoParam
     *            precio mínimo del evento.
     * @param precioMaximoParam
     *            precio máximo del evento.
     * @param fechaMinParam
     *            fecha mínima de creación del evento.
     * @param fechaMaxParam
     *            fecha máxima de creación del evento
     * @param categoriaParam
     *            categoría buscada del evento.
     * @param palabrasClaveParam
     *            palabras que definen características del evento buscado.
     *
     * @return lista de eventos que cumplan con los filtros utilizados, si los hay.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<EventoDTO> listarEventos(
            @RequestParam(value = "precioPesosMin", required = false) Double precioMinimoParam,
            @RequestParam(value = "precioPesosMax", required = false) Double precioMaximoParam,
            @RequestParam(value = "fechaInicioMin", required = false) LocalDate fechaMinParam,
            @RequestParam(value = "fechaInicioMax", required = false) LocalDate fechaMaxParam,
            @RequestParam(value = "categoria", required = false) String categoriaParam,
            @RequestParam(value = "palabrasClave", required = false) List<String> palabrasClaveParam) {
        if (precioMinimoParam == null && precioMaximoParam == null && fechaMinParam == null && fechaMaxParam == null
                && categoriaParam == null && palabrasClaveParam == null) {
            return eventoService.listarEventos().stream().map((Evento e) -> {
                return modelMapper.map(e, EventoDTO.class);
            }).toList();
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

            return eventoService.filtrarEventos(filtros).stream().map((Evento e) -> {
                return modelMapper.map(e, EventoDTO.class);
            }).toList();
        }
    }

    /**
     * Cambia el estado de un evento entre abierto y cerrado.
     *
     * @param email
     * @param eventoId
     * @param dto
     *
     * @return Respuesta vacía, con un status code de 204.
     */
    @PutMapping("/{eventoId}/estado")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> actualizarEstadoEvento(@AuthenticationPrincipal String email,
            @PathVariable String eventoId, EventoEstadoDTO dto) {

        var usuario = this.buscarUsuario(email);
        var evento = this.buscarEvento(eventoId);

        if (!evento.getOrganizador().equals(usuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El usuario no es organizador del evento");
        }

        if (dto.abierto()) {
            this.eventoService.abrirEvento(usuario, evento);
        } else {
            this.eventoService.cerrarEvento(usuario, evento);
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Devuelve las inscripciones para un evento.
     *
     * @param email
     * @param eventoId
     *
     * @return La lista de inscriptos.
     */
    @GetMapping("/{eventoId}/inscripciones")
    @ResponseStatus(HttpStatus.OK)
    public List<InscripcionResponse> getInscriptosAEvento(@AuthenticationPrincipal String email,
            @PathVariable String eventoId) {
        var usuario = this.buscarUsuario(email);
        var evento = this.buscarEvento(eventoId);
        if (!evento.getOrganizador().equals(usuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El usuario no es organizador del evento");
        }

        return this.inscripcionesService.buscarInscripcionesDeEvento(evento).stream()
                .filter((InscripcionEvento i) -> i.getEstado() == EstadoInscripcion.CONFIRMADA)
                .map((InscripcionEvento i) -> InscripcionResponse.confirmada(evento.getId())).toList();
    }

    /**
     * Devuelve la infromación sobre una inscripcion especifica. El usuario debe ser organizador del evento para poder
     * ver esto.
     *
     * @param email
     * @param eventoId
     * @param usuarioId
     *
     * @return La inscripcion solicitada.
     */
    @GetMapping("/{eventoId}/inscripciones/{usuarioId}")
    @ResponseStatus(HttpStatus.OK)
    public InscripcionResponse getInscripcion(@AuthenticationPrincipal String email, @PathVariable String eventoId,
            @PathVariable String usuarioId) {
        var usuario = this.buscarUsuario(email);
        var evento = this.buscarEvento(eventoId);
        if (!evento.getOrganizador().equals(usuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El usuario no es organizador del evento");
        }
        var inscripcion = this.buscarInscripcion(usuario, evento);
        return modelMapper.map(inscripcion, InscripcionResponse.class);

    }

    /**
     * Cancela una inscripción. El usuario debe ser organizador para poder ver esto. TODO: En un futuro, un usuario
     * normal también va a poder cancelar su inscripción con este método. Ahora mismo esa acción se realiza desde el
     * InscripcionesController.
     *
     * @param email
     * @param eventoId
     * @param usuarioId
     *
     * @return Status code No Content si fue cancelada correctamente.
     */
    @DeleteMapping("/{eventoId}/inscripciones/{usuarioId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> cancelarInscripcion(@AuthenticationPrincipal String email,
            @PathVariable String eventoId, @PathVariable String usuarioId) {
        var usuario = this.buscarUsuario(email);
        var evento = this.buscarEvento(eventoId);
        if (!evento.getOrganizador().equals(usuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El usuario no es organizador del evento");
        }
        inscripcionesService.cancelarInscripcion(evento, usuario);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Permite obtener las inscripciones en waitlist.
     *
     * @param email
     * @param eventoId
     *
     * @return Las inscripciones de la waitlist.
     */
    @GetMapping("/{eventoId}/waitlist")
    @ResponseStatus(HttpStatus.OK)
    public List<InscripcionEnWaitlistResponse> getWaitlistDeEvento(@AuthenticationPrincipal String email,
            @PathVariable String eventoId) {
        var usuario = this.buscarUsuario(email);
        var evento = this.buscarEvento(eventoId);
        if (!evento.getOrganizador().equals(usuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El usuario no es organizador del evento");
        }

        return this.inscripcionesService.buscarWaitlistDeEvento(evento).getItems().stream()
                .map((InscripcionEnWaitlist i) -> {
                    var usuarioResponse = modelMapper.map(i.getCandidato(), UsuarioResponse.class);
                    return new InscripcionEnWaitlistResponse(usuarioResponse, i.getFechaIngreso());
                }).toList();
    }

    private Usuario buscarUsuario(String email) {
        var optUsuario = usuarioService.buscarPorEmail(email);
        var usuario = optUsuario
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return usuario;
    }

    private Evento buscarEvento(String id) {
        var optEvento = this.eventoService.buscarEventoPorId(id);
        var evento = optEvento
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento no encontrado"));
        return evento;
    }

    private InscripcionEvento buscarInscripcion(Usuario usuario, Evento evento) {
        var optInscripcion = this.inscripcionesService.buscarInscripcion(usuario, evento);
        var inscripcion = optInscripcion
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inscripcion no encontrada"));
        return inscripcion;
    }
}
