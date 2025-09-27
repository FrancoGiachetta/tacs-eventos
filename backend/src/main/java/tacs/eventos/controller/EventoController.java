package tacs.eventos.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import tacs.eventos.controller.validadores.Validador;
import tacs.eventos.controller.validadores.ValidadorAutorizacionUsuario;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/evento")
@AllArgsConstructor
public class EventoController {

    private final EventoService eventoService;
    private final UsuarioService usuarioService;
    private final InscripcionesService inscripcionesService;
    private final ModelMapper modelMapper;

    /**
     * Crea un nuevo evento.
     *
     * @param dto
     *            datos del evento a crear.
     *
     * @return ResponseEntity devuelve el código 204 NO_CONTENT y un body vacio.
     *
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> crearEvento(@AuthenticationPrincipal Usuario usuario,
            @Valid @RequestBody CreacionEventoRequest dto, HttpServletRequest request) {
        Evento evento = modelMapper.map(dto, Evento.class);
        evento.setId(UUID.randomUUID().toString()); // No se estaba creando
        evento.setOrganizador(usuario);
        eventoService.crearEvento(evento);
        return ResponseEntity.created(URI.create(request.getRequestURI())).build();
    }

    /**
     * Devuelve el evento con el id en el url
     *
     * @param eventoId
     *            id del evento que se quiere obtener
     *
     * @return ResponseEntity devuelve el código 200 OK y un body con los datos del evento pedido. Si el evento se
     *         existe, devuelve NOT_FOUND 404.
     */
    @GetMapping("/{eventoId}")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Evento encontrado"),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado"), })
    public ResponseEntity<EventoResponse> obtenerEvento(@PathVariable String eventoId) {
        Evento evento = this.buscarEvento(eventoId);

        return ResponseEntity.ok(modelMapper.map(evento, EventoResponse.class));
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
     * @return ResponseEntity devuelve el código 200 OK y un body con la lista de eventos que cumplan con los filtros
     *         utilizados, si los hay.
     */
    @GetMapping
    @ApiResponse(responseCode = "200", description = "Lista de eventos disponibles")
    public ResponseEntity<List<EventoResponse>> listarEventos(
            @RequestParam(value = "precioPesosMin", required = false) Double precioMinimoParam,
            @RequestParam(value = "precioPesosMax", required = false) Double precioMaximoParam,
            @RequestParam(value = "fechaInicioMin", required = false) LocalDate fechaMinParam,
            @RequestParam(value = "fechaInicioMax", required = false) LocalDate fechaMaxParam,
            @RequestParam(value = "categoria", required = false) String categoriaParam,
            @RequestParam(value = "palabrasClave", required = false) String palabrasClaveParam) {
        if (precioMinimoParam == null && precioMaximoParam == null && fechaMinParam == null && fechaMaxParam == null
                && categoriaParam == null && palabrasClaveParam == null) {
            return ResponseEntity.ok(eventoService.listarEventos().stream()
                    .map((Evento e) -> modelMapper.map(e, EventoResponse.class)).toList());
        } else {
            List<FiltroBusqueda<Evento>> filtros = new ArrayList<>();

            // Solo agregar filtros si los parámetros están presentes
            if (fechaMinParam != null || fechaMaxParam != null) {
                filtros.add(new FiltradoPorFechaInicio(fechaMinParam, fechaMaxParam));
            }

            if (precioMinimoParam != null || precioMaximoParam != null) {
                filtros.add(new FiltradoPorPrecio(precioMinimoParam, precioMaximoParam));
            }

            if (categoriaParam != null && !categoriaParam.trim().isEmpty()) {
                filtros.add(new FiltradoPorCategoria(categoriaParam));
            }

            if (palabrasClaveParam != null && !palabrasClaveParam.trim().isEmpty()) {
                filtros.add(new FiltradoPorPalabrasClave(Arrays.asList(palabrasClaveParam.split("\\s+"))));
            }

            return ResponseEntity.ok(eventoService.filtrarEventos(filtros).stream()
                    .map(e -> modelMapper.map(e, EventoResponse.class)).toList());
        }
    }

    /**
     * Cambia el estado de un evento entre abierto y cerrado.
     *
     * @param usuario
     *            usuario logueado
     * @param eventoId
     *            id del evento cuyo estado se quiere actualizar
     * @param estadoDTO
     *            DTO representando el estado del evento
     *
     * @return ResponseEntity devuelve el código 204 NO_CONTENT y un body vacio. Si el evento se existe, devuelve
     *         NOT_FOUND 404.
     */
    @PutMapping("/{eventoId}/estado")
    @ApiResponse(responseCode = "404", description = "Evento no encontrado")
    public ResponseEntity<Void> actualizarEstadoEvento(@AuthenticationPrincipal Usuario usuario,
            @PathVariable String eventoId, EventoEstadoDTO estadoDTO) {
        Evento evento = this.buscarEvento(eventoId);

        Validador validador = new ValidadorAutorizacionUsuario(usuario, evento.getOrganizador());

        // Si el usuario no es el organizador, devolver 403.
        if (!validador.validar()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El usuario no está inscripto al evento");
        };

        if (estadoDTO.abierto()) {
            this.eventoService.abrirEvento(usuario, evento);
        } else {
            this.eventoService.cerrarEvento(usuario, evento);
        }

        return ResponseEntity.noContent().build();
    }

    /**
     * Devuelve las inscripciones para un evento.
     *
     * @param usuario
     *            usuario logueado
     * @param eventoId
     *            id del evento cuyas inscripciones se quiere consultar
     *
     * @return ResponseEntity devuelve el código 200 OK y un body con la lista de inscriptos solicitada. Si el evento se
     *         existe, devuelve NOT_FOUND 404.
     */
    @GetMapping("/{eventoId}/inscripcion")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Lista de incripciones para el evento"),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado"), })
    public ResponseEntity<List<InscripcionResponse>> getInscriptosAEvento(@AuthenticationPrincipal Usuario usuario,
            @PathVariable String eventoId) {
        Evento evento = this.buscarEvento(eventoId);

        Validador validador = new ValidadorAutorizacionUsuario(usuario, evento.getOrganizador());

        // Si el usuario no es el organizador, devolver 403.
        if (!validador.validar()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El usuario no es organizador");
        }

        return ResponseEntity.ok(this.inscripcionesService.buscarInscripcionesDeEvento(evento).stream()
                .filter((InscripcionEvento i) -> i.getEstado() == EstadoInscripcion.CONFIRMADA)
                .map((InscripcionEvento i) -> InscripcionResponse.confirmada(evento.getId())).toList());
    }

    /**
     * Devuelve la infromación sobre una inscripcion especifica. El usuario debe ser organizador del evento para poder
     * ver esto.
     *
     * @param usuarioLogueado
     *            usuario logueado al sistema
     * @param eventoId
     *            id del evento al que pertenece la inscripción que se está consultando
     * @param usuarioId
     *            id del usuario al que pertenece la inscripción que se está consultando
     *
     * @return ResponseEntity devuelve el código 200 OK y un body con la inscripcion solicitada. Si una se cumple alguna
     *         de las siguientes condiciones, devuelve NOT_FOUND 404: * El usuario buscado no esta inscripto. * El
     *         evento se existe. * El usuario que desato la accion no es el organizador.
     */
    @GetMapping("/{eventoId}/inscripcion/{usuarioId}")
    @ApiResponse(responseCode = "200", description = "Inscripcion encontrada")
    public ResponseEntity<InscripcionResponse> getInscripcion(@AuthenticationPrincipal Usuario usuarioLogueado,
            @PathVariable String eventoId, @PathVariable String usuarioId) {
        Evento evento = this.buscarEvento(eventoId);
        // Si el usuario no existe, retorno que no está inscripto para no revelar si existe o no el usuario
        Usuario usuarioInscripto = usuarioService.buscarPorId(usuarioId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario no está inscripto al evento"));

        List<Usuario> autorizados = Arrays.asList(evento.getOrganizador(), usuarioInscripto);

        Validador validador = new ValidadorAutorizacionUsuario(usuarioLogueado, autorizados);

        // Si el usuario no es el organizador, devolver 404. Devolvemos 404 para no revelar informacion sensible.
        if (!validador.validar()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        if (this.inscripcionesService.buscarInscripcionConfirmada(usuarioInscripto, evento).isPresent())
            return ResponseEntity.ok(InscripcionResponse.confirmada(evento.getId()));
        else if (this.inscripcionesService.inscripcionEstaEnWaitlist(evento, usuarioInscripto))
            return ResponseEntity.ok(InscripcionResponse.enWaitlist(evento.getId()));
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario no está inscripto al evento");
    }

    /**
     * Cancela una inscripción. Para poder ver esto, el usuario debe ser el organizador del evento o el mismo usuario
     * que se inscribió.
     * <p>
     * Hace un soft delete, no borra realmente la inscripción si no que la marca como cancelada.
     * <p>
     * Devuelve el status code 204 NO_CONTENT
     *
     * @param usuarioLogueado
     *            usuario logueado en el sistema
     * @param eventoId
     *            id del evento cuya inscripción se quiere cancelar
     * @param usuarioId
     *            id del usuario que se quiere desinscribir
     */
    @DeleteMapping("/{eventoId}/inscripcion/{usuarioId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> cancelarInscripcion(@AuthenticationPrincipal Usuario usuarioLogueado,
            @PathVariable String eventoId, @PathVariable String usuarioId) {
        Optional<Usuario> usuario = usuarioService.buscarPorId(usuarioId);
        Evento evento = this.buscarEvento(eventoId);
        
        // Si el usuario no existe, también retorno NO_CONTENT, para no revelar que existe el usuario
        usuario.ifPresent((u) -> {
            Validador validador = new ValidadorAutorizacionUsuario(usuarioLogueado,
                    List.of(evento.getOrganizador(), u));

            // Solo hago la acción si el usuario está autorizado. Si no está autorizado, digo igual NO_CONTENT, para no
            // revelar información sensible (si el usuario existe, si está inscripto, etc)
            if (validador.validar()) {
                inscripcionesService.cancelarInscripcion(evento, u);
            }
        });

        return ResponseEntity.noContent().build();
    }

    /**
     * Inscribe a un usuario a un evento. El único usuario que puede inscribirse es él mismo.
     *
     * @param usuarioLogueado
     *            usuario logueado al sistema
     * @param eventoId
     *            id del evento sobre el cual se quiere crear una inscripción
     *
     * @return ResponseEntity devuelve el código 201 CREATED y un body vacío
     */
    @PostMapping("/{eventoId}/inscripcion/")
    @ResponseStatus(HttpStatus.CREATED) // TODO: crear un id de inscripción y retornar el location
    public ResponseEntity<Void> inscribirUsuarioAEvento(@AuthenticationPrincipal Usuario usuarioLogueado,
            @PathVariable String eventoId, HttpServletRequest request) {
        Evento evento = this.buscarEvento(eventoId);

        // Si el usuario ya está inscripto o en la waitlist, no hace nada y devuelve la inscripción existente con el
        // código 200 OK
        if (inscripcionesService.inscripcionConfirmadaOEnWaitlist(evento, usuarioLogueado))
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .location(URI.create(request.getRequestURI().concat(usuarioLogueado.getId()))).build();

        // Si no estaba inscripto, intenta inscribirlo o mandarlo a la waitlist
        inscripcionesService.inscribirOMandarAWaitlist(evento, usuarioLogueado);
        return ResponseEntity.created(URI.create(request.getRequestURI())).build();
    }

    /**
     * Permite obtener las inscripciones en waitlist.
     *
     * @param usuario
     *            usuario logueado al sistema
     * @param eventoId
     *            id del evento cuya waitlist se quiere consultar
     *
     * @return Las inscripciones de la waitlist.
     */
    @GetMapping("/{eventoId}/waitlist")
    @ApiResponse(responseCode = "200", description = "Lista de incripciones en waitlist para el evento")
    public ResponseEntity<List<InscripcionEnWaitlistResponse>> getWaitlistDeEvento(
            @AuthenticationPrincipal Usuario usuario, @PathVariable String eventoId) {
        Evento evento = this.buscarEvento(eventoId);

        Validador validador = new ValidadorAutorizacionUsuario(usuario, evento.getOrganizador());

        // Si el usuario no es el organizador, devolver 403.
        if (!validador.validar()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El usuario no es organizador");
        }

        return ResponseEntity.ok(this.inscripcionesService.buscarWaitlistDeEvento(evento).getItems().stream()
                .map((InscripcionEnWaitlist i) -> {
                    var usuarioResponse = modelMapper.map(i.getCandidato(), UsuarioResponse.class);
                    return new InscripcionEnWaitlistResponse(usuarioResponse, i.getFechaIngreso());
                }).toList());
    }

    private Evento buscarEvento(String id) {
        return this.eventoService.buscarEventoPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento no encontrado"));
    }
}
