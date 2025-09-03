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

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/evento")
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
     * @param dto datos del evento a crear.
     * @return datos del evento creado.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> crearEvento(@AuthenticationPrincipal Usuario usuario, @Valid @RequestBody EventoDTO dto) {
        Evento evento = mapper.map(dto, Evento.class);
        evento.setOrganizador(usuario);
        var eventoCreado = eventoService.crearEvento(evento);
        var location = URI.create(String.format("/api/v1/evento/%s", eventoCreado.getId()));
        return ResponseEntity.created(location).build();
    }

    /**
     * Devuelve el evento con el id en el url
     *
     * @param eventoId
     * @return los datos del evento pedido
     */
    @GetMapping("/{eventoId}")
    @ResponseStatus(HttpStatus.OK)
    public EventoDTO obtenerEvento(@PathVariable String eventoId) {
        var evento = this.buscarEvento(eventoId);
        return modelMapper.map(evento, EventoDTO.class);
    }

    /**
     * Devuelve todos los eventos vigentes. Aplica filtros si los hubiera.
     *
     * @param precioMinimoParam  precio mínimo del evento.
     * @param precioMaximoParam  precio máximo del evento.
     * @param fechaMinParam      fecha mínima de creación del evento.
     * @param fechaMaxParam      fecha máxima de creación del evento
     * @param categoriaParam     categoría buscada del evento.
     * @param palabrasClaveParam palabras que definen características del evento buscado.
     * @return lista de eventos que cumplan con los filtros utilizados, si los hay.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventoDTO> listarEventos(
            @RequestParam(value = "precioPesosMin", required = false) Double precioMinimoParam,
            @RequestParam(value = "precioPesosMax", required = false) Double precioMaximoParam,
            @RequestParam(value = "fechaInicioMin", required = false) LocalDate fechaMinParam,
            @RequestParam(value = "fechaInicioMax", required = false) LocalDate fechaMaxParam,
            @RequestParam(value = "categoria", required = false) String categoriaParam,
            @RequestParam(value = "palabrasClave", required = false) List<String> palabrasClaveParam) {
        if (precioMinimoParam == null && precioMaximoParam == null && fechaMinParam == null && fechaMaxParam == null
                && categoriaParam == null && palabrasClaveParam == null) {
            return eventoService.listarEventos().stream().map((Evento e) -> modelMapper.map(e, EventoDTO.class))
                    .toList();
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

            return eventoService.filtrarEventos(filtros).stream().map(e -> modelMapper.map(e, EventoDTO.class))
                    .toList();
        }
    }

    /**
     * Cambia el estado de un evento entre abierto y cerrado.
     *
     * @param usuario
     * @param eventoId
     * @param dto
     * @return Respuesta vacía, con un status code de 204.
     */
    @PutMapping("/{eventoId}/estado")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> actualizarEstadoEvento(@AuthenticationPrincipal Usuario usuario,
                                                       @PathVariable String eventoId, EventoEstadoDTO dto) {

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
     * @param usuario
     * @param eventoId
     * @return La lista de inscriptos.
     */
    @GetMapping("/{eventoId}/inscripcion")
    @ResponseStatus(HttpStatus.OK)
    public List<InscripcionResponse> getInscriptosAEvento(@AuthenticationPrincipal Usuario usuario,
                                                          @PathVariable String eventoId) {
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
     * @param usuarioLogueado
     * @param eventoId
     * @param usuarioId
     * @return La inscripcion solicitada.
     */
    @GetMapping("/{eventoId}/inscripcion/{usuarioId}")
    @ResponseStatus(HttpStatus.OK)
    public InscripcionResponse getInscripcion(@AuthenticationPrincipal Usuario usuarioLogueado, @PathVariable String eventoId,
                                              @PathVariable String usuarioId) {
        var evento = this.buscarEvento(eventoId);
        var usuarioInscripto = this.buscarUsuarioPorId(usuarioId);
        if (!evento.getOrganizador().equals(usuarioLogueado) && !usuarioInscripto.equals(usuarioLogueado)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "El usuario no es organizador del evento y tampoco " + "es el dueño de la inscripción");
        }
        boolean inscripcionExistente = this.inscripcionesService.buscarInscripcion(usuarioInscripto, evento)
                .isPresent();
        boolean inscripcionEnWaitlist = this.inscripcionesService.inscripcionEstaEnWaitlist(evento, usuarioInscripto);
        if (inscripcionExistente)
            return InscripcionResponse.confirmada(evento.getId());
        else if (inscripcionEnWaitlist)
            return InscripcionResponse.enWaitlist(evento.getId());
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario no está inscripto al evento");
    }

    /**
     * Cancela una inscripción. Para poder ver esto, el usuario debe ser el organizador del evento o el mismo usuario
     * que se inscribió.
     * <p>
     * Hace un soft delete, no borra realmente la inscripción si no que la marca como cancelada.
     *
     * @param usuarioLogueado usuario logueado en el sistema
     * @param eventoId        id del evento cuya inscripción se quiere cancelar
     * @param usuarioId       id del usuario que se quiere desinscribir
     * @return Status code No Content si fue cancelada correctamente.
     */
    @DeleteMapping("/{eventoId}/inscripcion/{usuarioId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelarInscripcion(@AuthenticationPrincipal Usuario usuarioLogueado, @PathVariable String eventoId,
                                    @PathVariable String usuarioId) {
        var usuarioADesinscribir = this.buscarUsuarioPorId(usuarioId);
        var evento = this.buscarEvento(eventoId);
        if (!usuarioLogueado.equals(usuarioADesinscribir) && !usuarioLogueado.equals(evento.getOrganizador())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Los únicos usuarios que pueden cancelar una "
                    + "inscripción son el usuario que se inscribió, y el organizador del evento");
        }
        inscripcionesService.cancelarInscripcion(evento, usuarioADesinscribir);
    }

    /**
     * Inscribe a un usuario a un evento. El único usuario que puede inscribirse es él mismo.
     *
     * @param usuarioLogueado
     * @param eventoId
     * @param usuarioId
     * @return ResponseEntity Un body vacío con la ubicación de la inscripción en el location header. Si se creó
     * exitosamente, devuelve el código 201 CREATED. Si la inscripción ya existía, devuelve el código 303 SEE OTHER.
     */
    @PostMapping("/{eventoId}/inscripcion/{usuarioId}")
    public ResponseEntity<Void> inscribirUsuarioAEvento(@AuthenticationPrincipal Usuario usuarioLogueado,
                                                        @PathVariable String eventoId, @PathVariable String usuarioId) {
        var usuarioAInscribir = this.buscarUsuarioPorId(usuarioId);
        var evento = this.buscarEvento(eventoId);
        /*
         * TODO: ver si no se puede hacer esta validación en alguna forma distinta, con alguna herramienta de Spring,
         * porque está repetida en todos los métodos
         */
        /*
         * TODO: retornar otro código de error, esto es inseguro porque estamos avisando a un posible atacante que ese
         * usuario existe
         */
        if (!usuarioLogueado.equals(usuarioAInscribir) && !usuarioLogueado.equals(evento.getOrganizador())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Solamente pueden crear una inscripción el usuario que se va a inscribir, o el "
                            + "organizador del evento");
        }
        var location = URI.create(String.format("/api/v1/evento/%s/inscripcion/%s", eventoId, usuarioId));
        // Si el usuario ya está inscripto o en la waitlist, no hace nada y devuelve la inscripción existente con el
        // código 200 OK
        if (inscripcionesService.inscripcionConfirmadaOEnWaitlist(evento, usuarioAInscribir))
            return ResponseEntity.status(HttpStatus.SEE_OTHER).location(location).build();

        // Si no estaba inscripto, intenta inscribirlo o mandarlo a la waitlist
        var resultadoInscripcion = inscripcionesService.inscribirOMandarAWaitlist(evento, usuarioAInscribir);
        return resultadoInscripcion.isPresent() ? ResponseEntity.created(location).build()
                : ResponseEntity.status(HttpStatus.SEE_OTHER).location(location).build();
    }

    /**
     * Permite obtener las inscripciones en waitlist.
     *
     * @param usuario
     * @param eventoId
     * @return Las inscripciones de la waitlist.
     */
    @GetMapping("/{eventoId}/waitlist")
    @ResponseStatus(HttpStatus.OK)
    public List<InscripcionEnWaitlistResponse> getWaitlistDeEvento(@AuthenticationPrincipal Usuario usuario,
                                                                   @PathVariable String eventoId) {
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

    private Usuario buscarUsuarioPorId(String id) {
        var optUsuario = usuarioService.buscarPorId(id);
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
}
