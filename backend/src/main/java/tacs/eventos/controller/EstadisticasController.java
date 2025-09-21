package tacs.eventos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tacs.eventos.service.EstadisticaService;
import tacs.eventos.service.EventoService;

@RestController
@RequestMapping("/api/v1/estadisticas")
@AllArgsConstructor
public class EstadisticasController {

    private final EventoService eventoService;

    private final EstadisticaService estadisticaService;

    // TODO: priorizar performance
    @GetMapping("/eventos/total")
    @Operation(summary = "Devuelve la cantidad de eventos registrados en el sistema")
    @ApiResponse(responseCode = "200", description = "OK")
    public int cantidadEventos() throws Exception {
        return estadisticaService.cantidadEventos();
    }

    @GetMapping("/inscripciones/total")
    @Operation(summary = "Devuelve la cantidad de de inscripciones registrados en todo sistema")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<Integer> cantidadInscripciones() {
        return ResponseEntity.ok(estadisticaService.cantidadInscribiciones());
    }

    @GetMapping("/eventos/{eventoId}/tasa-conversionwl")
    @Operation(summary = "Devuelve la tasa de conversion de wait list de un evento")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<Integer> tasaConversionWL(@PathVariable String eventoId) {
        return ResponseEntity.ok(estadisticaService.calcularTasaConversionWL(eventoId));
    }

    // TODO: realizar futuras estadiscas

}
