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
     * @return datos del evento creado.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> crearEvento(@AuthenticationPrincipal Usuario usuario,
            @Valid @RequestBody CreacionEventoRequest dto, HttpServletRequest request) {
        Evento evento = modelMapper.map(dto, Evento.class);
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
     * @return los datos del evento pedido
     */
    @GetMapping("/{eventoId}")
    @ResponseStatus(HttpStatus.OK)
    public EventoResponse obtenerEvento(@PathVariable String eventoId) {
        var evento = this.buscarEvento(eventoId);
        return modelMapper.map(evento, EventoResponse.class);
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
    @ResponseStatus(HttpStatus.OK)
    public List<EventoResponse> listarEventos(
            @RequestParam(value = "precioPesosMin", required = false) Double precioMinimoParam,
            @RequestParam(value = "precioPesosMax", required = false) Double precioMaximoParam,
            @RequestParam(value = "fechaInicioMin", required = false) LocalDate fechaMinParam,
            @RequestParam(value = "fechaInicioMax", required = false) LocalDate fechaMaxParam,
            @RequestParam(value = "categoria", required = false) String categoriaParam,
            @RequestParam(value = "palabrasClave", required = false) String palabrasClaveParam) {
        if (precioMinimoParam == null && precioMaximoParam == null && fechaMinParam == null && fechaMaxParam == null
                && categoriaParam == null && palabrasClaveParam == null) {
            return eventoService.listarEventos().stream().map((Evento e) -> modelMapper.map(e, EventoResponse.class))
                    .toList();
        } else {
            List<FiltroBusqueda<Evento>> filtros = new ArrayList<>();

            // Solo agregar filtros si los parámetros están presentes
            if (fechaMinParam != null || fechaMaxParam != null) {
                LocalDate fechaMinima = fechaMinParam != null ? fechaMinParam : LocalDate.now();
                LocalDate fechaMaxima = fechaMaxParam != null ? fechaMaxParam : LocalDate.MAX;
                filtros.add(new FiltradoPorFechaInicio(fechaMinima, fechaMaxima));
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

            return eventoService.filtrarEventos(filtros).stream().map(e -> modelMapper.map(e, EventoResponse.class))
                    .toList();
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
     * @return Respuesta vacía, con un status code de 204.
     */
    @PutMapping("/{eventoId}/estado")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> actualizarEstadoEvento(@AuthenticationPrincipal Usuario usuario,
            @PathVariable String eventoId, EventoEstadoDTO estadoDTO) {
        var evento = this.buscarEvento(eventoId);
        verificarAutorizacion(usuario, "El usuario no es organizador del evento", false, evento.getOrganizador());

        if (estadoDTO.abierto()) {
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
     *            usuario logueado
     * @param eventoId
     *            id del evento cuyas inscripciones se quiere consultar
     *
     * @return La lista de inscriptos.
     */
    @GetMapping("/{eventoId}/inscripcion")
    @ResponseStatus(HttpStatus.OK)
    public List<InscripcionResponse> getInscriptosAEvento(@AuthenticationPrincipal Usuario usuario,
            @PathVariable String eventoId) {
        var evento = this.buscarEvento(eventoId);
        verificarAutorizacion(usuario, "El usuario no es organizador del evento", false, evento.getOrganizador());

        return this.inscripcionesService.buscarInscripcionesDeEvento(evento).stream()
                .filter((InscripcionEvento i) -> i.getEstado() == EstadoInscripcion.CONFIRMADA)
                .map((InscripcionEvento i) -> InscripcionResponse.confirmada(evento.getId())).toList();
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
     * @return La inscripcion solicitada.
     */
    @GetMapping("/{eventoId}/inscripcion/{usuarioId}")
    @ResponseStatus(HttpStatus.OK)
    public InscripcionResponse getInscripcion(@AuthenticationPrincipal Usuario usuarioLogueado,
            @PathVariable String eventoId, @PathVariable String usuarioId) {
        String mensajeNoEncontrado = "El usuario no está inscripto al evento";

        var evento = this.buscarEvento(eventoId);
        // Si el usuario no existe, retorno que no está inscripto para no revelar si existe o no el usuario
        var usuarioInscripto = usuarioService.buscarPorId(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, mensajeNoEncontrado));

        verificarAutorizacion(usuarioLogueado, mensajeNoEncontrado, true, evento.getOrganizador(), usuarioInscripto);

        if (this.inscripcionesService.buscarInscripcionConfirmada(usuarioInscripto, evento).isPresent())
            return InscripcionResponse.confirmada(evento.getId());
        else if (this.inscripcionesService.inscripcionEstaEnWaitlist(evento, usuarioInscripto))
            return InscripcionResponse.enWaitlist(evento.getId());
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, mensajeNoEncontrado);
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
    public void cancelarInscripcion(@AuthenticationPrincipal Usuario usuarioLogueado, @PathVariable String eventoId,
            @PathVariable String usuarioId) {
        var optUsuario = usuarioService.buscarPorId(usuarioId);
        var evento = this.buscarEvento(eventoId);

        // Si el usuario no existe, también retorno NO_CONTENT, para no revelar que existe el usuario
        optUsuario.ifPresent(u -> {
            // Solo hago la acción si el usuario está autorizado. Si no está autorizado, digo igual NO_CONTENT, para no
            // revelar información sensible (si el usuario existe, si está inscripto, etc)
            if (estaEntreLosAutorizados(usuarioLogueado, List.of(evento.getOrganizador(), optUsuario.get())))
                inscripcionesService.cancelarInscripcion(evento, optUsuario.get());
        });
    }

    /**
     * Inscribe a un usuario a un evento. El único usuario que puede inscribirse es él mismo.
     *
     * @param usuarioLogueado
     *            usuario logueado al sistema
     * @param eventoId
     *            id del evento sobre el cual se quiere crear una inscripción
     * @param usuarioId
     *            id del usuario que se quiere inscribir
     *
     * @return ResponseEntity Un body vacío con la ubicación de la inscripción en el location header. Si se creó
     *         exitosamente, devuelve el código 201 CREATED. Si la inscripción ya existía, devuelve el código 303 SEE
     *         OTHER.
     */
    @PostMapping("/{eventoId}/inscripcion/{usuarioId}")
    public ResponseEntity<Void> inscribirUsuarioAEvento(@AuthenticationPrincipal Usuario usuarioLogueado,
            @PathVariable String eventoId, @PathVariable String usuarioId, HttpServletRequest request) {
        var optUsuarioAInscribir = usuarioService.buscarPorId(usuarioId);
        var evento = this.buscarEvento(eventoId);
        // Si el usuario no existe, también retorno FORBIDDEN, para no revelar que existe el usuario
        if (optUsuarioAInscribir.isEmpty() || !estaEntreLosAutorizados(usuarioLogueado,
                List.of(evento.getOrganizador(), optUsuarioAInscribir.get()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Solamente pueden crear una inscripción el usuario que se va a inscribir, o el "
                            + "organizador del evento");
        }
        var usuarioAInscribir = optUsuarioAInscribir.get();
        // Si el usuario ya está inscripto o en la waitlist, no hace nada y devuelve la inscripción existente con el
        // código 200 OK
        if (inscripcionesService.inscripcionConfirmadaOEnWaitlist(evento, usuarioAInscribir))
            return ResponseEntity.status(HttpStatus.SEE_OTHER).location(URI.create(request.getRequestURI())).build();

        // Si no estaba inscripto, intenta inscribirlo o mandarlo a la waitlist
        inscripcionesService.inscribirOMandarAWaitlist(evento, usuarioAInscribir);
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
    @ResponseStatus(HttpStatus.OK)
    public List<InscripcionEnWaitlistResponse> getWaitlistDeEvento(@AuthenticationPrincipal Usuario usuario,
            @PathVariable String eventoId) {
        var evento = this.buscarEvento(eventoId);
        verificarAutorizacion(usuario, "El usuario no es organizador del evento", false, evento.getOrganizador());

        return this.inscripcionesService.buscarWaitlistDeEvento(evento).getItems().stream()
                .map((InscripcionEnWaitlist i) -> {
                    var usuarioResponse = modelMapper.map(i.getCandidato(), UsuarioResponse.class);
                    return new InscripcionEnWaitlistResponse(usuarioResponse, i.getFechaIngreso());
                }).toList();
    }

    private Evento buscarEvento(String id) {
        return this.eventoService.buscarEventoPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento no encontrado"));
    }

    /**
     * Verifica que el usuario autenticado esté dentro de los autorizados para la acción que quiere realizar.
     * <p>
     * Si no lo está, lanza una excepción con el código 403 FORBIDDEN, o 404 NOT_FOUND si se indicó así en el parámetro
     * correspondiente. Esto último es útil para no revelar información sensible, como por ejemplo si un usuario existe,
     * o si está inscrito a un evento.
     *
     * @param autenticado
     *            usuario autenticado en el sistema
     * @param mensajeError
     *            mensaje de error que se quiere mostrar al usuario. Si retornarNotFound es true, este debe ser el mismo
     *            mensaje que retorna el endpoint cuando el recurso no existe.
     * @param retornarNotFound
     *            si se prefiere retornar 404 en vez de 403 (para no revelar información sensible)
     * @param autorizados
     *            usuarios con permiso para realizar la acción
     */
    private void verificarAutorizacion(Usuario autenticado, String mensajeError, boolean retornarNotFound,
            Usuario... autorizados) {
        if (!estaEntreLosAutorizados(autenticado, Arrays.asList(autorizados)))
            throw new ResponseStatusException(retornarNotFound ? HttpStatus.NOT_FOUND : HttpStatus.FORBIDDEN,
                    mensajeError);
    }

    private boolean estaEntreLosAutorizados(Usuario autenticado, List<Usuario> autorizados) {
        return autorizados.stream().anyMatch(autenticado::equals);
    }
}
