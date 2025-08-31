package tacs.eventos.controller;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tacs.eventos.dto.EventoDTO;
import tacs.eventos.dto.EventoEstadoDTO;
import tacs.eventos.model.Evento;
import tacs.eventos.model.Usuario;
import tacs.eventos.model.Waitlist;
import tacs.eventos.model.inscripcion.InscripcionEvento;
import tacs.eventos.repository.FiltroBusqueda;
import tacs.eventos.repository.evento.busqueda.FiltradoPorCategoria;
import tacs.eventos.repository.evento.busqueda.FiltradoPorFechaInicio;
import tacs.eventos.repository.evento.busqueda.FiltradoPorPalabrasClave;
import tacs.eventos.repository.evento.busqueda.FiltradoPorPrecio;
import tacs.eventos.service.EventoService;
import tacs.eventos.service.InscripcionesService;
import tacs.eventos.service.SessionService;
import tacs.eventos.service.UsuarioService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/eventos")
@AllArgsConstructor
public class EventoController {

    private final EventoService eventoService;
    private final UsuarioService usuarioService;
    private final InscripcionesService inscripcionesService;

    private final ModelMapper mapper;

    @PostMapping
    public Evento crearEvento(@AuthenticationPrincipal String email, @RequestBody EventoDTO dto) {
        var optUsuario = usuarioService.buscarPorEmail(email);
        var usuario = optUsuario.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario no encontrado"));

        Evento evento = mapper.map(dto, Evento.class);
        evento.setOrganizador(usuario);

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

    @PutMapping("/{eventoId}/estado")
    public ResponseEntity<Map<String, Object>> actualizarEstadoEvento(
        @AuthenticationPrincipal String email,
        @PathVariable String eventoId,
        EventoEstadoDTO dto) {

        var usuario = this.buscarUsuario(email);
        var evento = this.buscarEvento(eventoId);

        if (!evento.getOrganizador().equals(usuario)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario no es organizador del evento");
        }

        if (dto.abierto()) {
            this.eventoService.abrirEvento(usuario, evento);
        } else {
            this.eventoService.cerrarEvento(usuario, evento);
        }

        Map<String, Object> body = new HashMap<>();
        body.put("acknowledged", true);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @GetMapping("/{eventoId}/inscripciones")
    public List<InscripcionEvento> getInscriptosAEvento(
        @AuthenticationPrincipal String email,
        @PathVariable String eventoId
    ) {
        var usuario = this.buscarUsuario(email);
        var evento = this.buscarEvento(eventoId);
        if (!evento.getOrganizador().equals(usuario)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario no es organizador del evento");
        }

        return this.inscripcionesService.buscarInscripcionesDeEvento(evento);
    }

    @GetMapping("/{eventoId}/waitlist")
    public Waitlist getWaitlistDeEvento(
        @AuthenticationPrincipal String email,
        @PathVariable String eventoId
    ) {
        var usuario = this.buscarUsuario(email);
        var evento = this.buscarEvento(eventoId);
        if (!evento.getOrganizador().equals(usuario)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario no es organizador del evento");
        }

        return this.inscripcionesService.buscarWaitlistDeEvento(evento);
    }

    @GetMapping("/{eventoId}/inscripciones/{usuarioId}")
    public InscripcionEvento getInscripcion(
        @AuthenticationPrincipal String email,
        @PathVariable String eventoId,
        @PathVariable String usuarioId
    ) {
        var usuario = this.buscarUsuario(email);
        var evento = this.buscarEvento(eventoId);
        if (!evento.getOrganizador().equals(usuario)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario no es organizador del evento");
        }
        return this.buscarInscripcion(usuario, evento);
    }

    @DeleteMapping("/{eventoId}/inscripciones/{usuarioId}")
    public ResponseEntity<Void> cancelarInscripcion(
        @AuthenticationPrincipal String email,
        @PathVariable String eventoId,
        @PathVariable String usuarioId
    ) {
        var usuario = this.buscarUsuario(email);
        var evento = this.buscarEvento(eventoId);
        if (!evento.getOrganizador().equals(usuario)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario no es organizador del evento");
        }
        inscripcionesService.cancelarInscripcion(evento, usuario);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{eventoId}/waitlist/{usuarioId}")
    public ResponseEntity<Void> cancelarInscripcionEnWaitlist(
        @AuthenticationPrincipal String email,
        @PathVariable String eventoId,
        @PathVariable String usuarioId
    ) {
        var usuario = this.buscarUsuario(email);
        var evento = this.buscarEvento(eventoId);
        if (!evento.getOrganizador().equals(usuario)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario no es organizador del evento");
        }
        var waitlist = inscripcionesService.buscarWaitlistDeEvento(evento);
        waitlist.anularInscripcion(usuario);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    private Usuario buscarUsuario(String email) {
        var optUsuario = usuarioService.buscarPorEmail(email);
        var usuario = optUsuario.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario no encontrado"));
        return usuario;
    }

    private Evento buscarEvento(String id) {
        var optEvento = this.eventoService.buscarEventoPorId(id);
        var evento = optEvento.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Evento no encontrado"));
        return evento;
    }

    private InscripcionEvento buscarInscripcion(Usuario usuario, Evento evento) {
        var optInscripcion = this.inscripcionesService.buscarInscripcion(usuario, evento);
        var inscripcion = optInscripcion.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Inscripcion no encontrada"));
        return inscripcion;
    }
}
