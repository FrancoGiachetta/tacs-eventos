package tacs.eventos.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tacs.eventos.dto.InscripcionRequest;
import tacs.eventos.dto.InscripcionResponse;
import tacs.eventos.repository.EventosRepository;
import tacs.eventos.repository.UsuarioRepository;
import tacs.eventos.service.InscripcionesService;

@RestController
@RequestMapping("/api/inscripciones")
@AllArgsConstructor
public class InscripcionesController {
    private final UsuarioRepository usuarioRepository;
    private final EventosRepository eventosRepository;
    private final InscripcionesService inscripcionesService;

    /**
     * Crea una nueva inscripción para un evento. Retorna siempre Created y la inscripción, aunque la inscripción ya
     * estuviera realizada y este métod0 no haya hecho nada
     *
     * @param inscripcionRequest
     *
     * @return la inscripción creada
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<InscripcionResponse> crearInscripcion(@RequestBody InscripcionRequest inscripcionRequest) {
        var optUsuario = usuarioRepository.obtenerPorId(inscripcionRequest.usuarioId());
        var optEvento = eventosRepository.getEvento(inscripcionRequest.eventoId());

        var usuario = optUsuario
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario no encontrado"));
        var evento = optEvento
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Evento no encontrado"));

        // Si el usuario ya está inscripto o en la waitlist, no hace nada y devuelve la inscripción existente con el
        // código 200 OK
        if (inscripcionesService.inscripcionEstaConfirmada(evento, usuario))
            return ResponseEntity.status(HttpStatus.OK)
                    .body(InscripcionResponse.confirmada(usuario.getId(), evento.getId()));

        if (inscripcionesService.inscripcionEstaEnWaitlist(evento, usuario))
            return ResponseEntity.status(HttpStatus.OK)
                    .body(InscripcionResponse.enWaitlist(usuario.getId(), evento.getId()));

        // Si no estaba inscripto, intenta inscribirlo o mandarlo a la waitlist, y retorna 201 CREATED y la inscripción
        var resultadoInscripcion = inscripcionesService.inscribirOMandarAWaitlist(evento, usuario);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resultadoInscripcion.isPresent() ? InscripcionResponse.confirmada(usuario.getId(), evento.getId())
                        : InscripcionResponse.enWaitlist(usuario.getId(), evento.getId()));
    }

    /**
     * Cancela una inscripción. Hace un soft delete, no borra realmente la inscripción si no que la marca como
     * cancelada.
     *
     * @param inscripcionRequest
     *
     * @return NO_CONTENT, porque desde el punto de vista del usuario la inscripción ya no existe. Nosotros la marcamos
     *         como cancelada en vez de eliminarla, para obtener métricas.
     */
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelarInscripcion(@RequestBody InscripcionRequest inscripcionRequest) {
        var optUsuario = usuarioRepository.obtenerPorId(inscripcionRequest.usuarioId());

        var usuario = optUsuario
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario no encontrado"));
        var evento = eventosRepository.getEvento(inscripcionRequest.eventoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Evento no encontrado"));

        inscripcionesService.cancelarInscripcion(evento, usuario);
    }

}
