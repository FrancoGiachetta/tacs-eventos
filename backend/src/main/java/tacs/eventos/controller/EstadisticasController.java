package tacs.eventos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tacs.eventos.service.EstadisticaService;
import tacs.eventos.service.EventoService;
import tacs.eventos.service.SessionService;
import tacs.eventos.service.UsuarioService;
import tacs.eventos.controller.error.handlers.AccesoDenegadoHandler;
import tacs.eventos.model.RolUsuario;

@RestController
@RequestMapping("/api/v1/estadisticas")
@AllArgsConstructor
public class EstadisticasController {

    private final EventoService eventoService;
    private final EstadisticaService estadisticaService;
    private final SessionService sessionService;
    private final UsuarioService usuarioService;

    // TODO: priorizar performance
    @GetMapping("/eventos/total")
    @Operation(summary = "Devuelve la cantidad de eventos registrados en el sistema")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "OK") })
    public ResponseEntity<Integer> cantidadEventos(@RequestHeader("Authorization") String authHeader) throws Exception {
        // Validar que el usuario es admin
        if (!esUsuarioAdmin(authHeader)) {
            throw new AccesoDenegadoHandler("Acceso denegado - Solo admin");
        }
        return ResponseEntity.ok(estadisticaService.cantidadEventos());
    }

    @GetMapping("/inscripciones/total")
    @Operation(summary = "Devuelve la cantidad de de inscripciones registrados en todo sistema")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "OK") })
    public ResponseEntity<Integer> cantidadInscripciones(@RequestHeader("Authorization") String authHeader) {
        // Validar que el usuario es admin
        if (!esUsuarioAdmin(authHeader)) {
            throw new AccesoDenegadoHandler("Acceso denegado - Solo admin");
        }
        return ResponseEntity.ok(estadisticaService.cantidadInscribiciones());
    }

    @GetMapping("/eventos/{eventoId}/tasa-conversionwl")
    @Operation(summary = "Devuelve la tasa de conversion de wait list de un evento")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "OK") })
    public ResponseEntity<Integer> tasaConversionWL(@PathVariable String eventoId,
            @RequestHeader("Authorization") String authHeader) {
        // Validar que el usuario es admin
        if (!esUsuarioAdmin(authHeader)) {
            throw new AccesoDenegadoHandler("Acceso denegado - Solo admin");
        }
        return ResponseEntity.ok(estadisticaService.calcularTasaConversionWL(eventoId));
    }

    /**
     * Valida si el usuario autenticado es administrador
     */
    private boolean esUsuarioAdmin(String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            var usuarioOpt = sessionService.validate(token);
            return usuarioOpt.isPresent() && usuarioOpt.get().getRoles().contains(RolUsuario.ADMIN);
        } catch (Exception e) {
            return false;
        }
    }

    // TODO: realizar futuras estadiscas

}
