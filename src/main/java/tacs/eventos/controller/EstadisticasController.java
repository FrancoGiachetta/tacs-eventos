package tacs.eventos.controller;


import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tacs.eventos.service.EstadisticaService;
import tacs.eventos.service.EventoService;

@RestController
@RequestMapping("/api/estadisticas")
@AllArgsConstructor
public class EstadisticasController {

private final EventoService eventoService;



private final EstadisticaService estadisticaService;


//TODO:  priorizar performance
@GetMapping("/cantidad-eventos")
@Operation(summary = "Devuelve la cantidad de eventos registrados en el sistema")

public int cantidadEventos() {
    return estadisticaService.cantidadEventos();
}


@GetMapping("/cantidad-inscripciones")
@Operation(summary = "Devuelve la cantidad de de inscripciones registrados en todo sistema")

public int cantidadInscripciones() {
    return estadisticaService.cantidadInscribiciones();
}


@GetMapping("/{eventoId}/tasa-conversionwl")
@Operation(summary = "Devuelve la tasa de conversion de wait list de un evento")

public int tasaConversionWL(@PathVariable String eventoId) {

    return estadisticaService.calcularTasaConversionWL(eventoId);

}

//TODO: realizar futuras estadiscas

}




