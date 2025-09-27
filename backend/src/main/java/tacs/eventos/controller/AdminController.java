package tacs.eventos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tacs.eventos.dto.UsuarioDto;
import tacs.eventos.dto.CambiarRolRequest;
import tacs.eventos.model.RolUsuario;
import tacs.eventos.model.Usuario;
import tacs.eventos.service.UsuarioService;
import tacs.eventos.service.SessionService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UsuarioService usuarioService;
    private final SessionService sessionService;

    @GetMapping("/usuarios")
    @Operation(summary = "Obtener todos los usuarios (solo admin)")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado") })
    public ResponseEntity<List<UsuarioDto>> obtenerTodosLosUsuarios(@RequestHeader("Authorization") String authHeader) {

        // Validar que el usuario es admin
        if (!esUsuarioAdmin(authHeader)) {
            return ResponseEntity.status(403).build();
        }

        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();
        List<UsuarioDto> usuariosDto = usuarios.stream().map(this::convertirADto).collect(Collectors.toList());
        return ResponseEntity.ok(usuariosDto);
    }

    @PutMapping("/usuarios/{usuarioId}/rol")
    @Operation(summary = "Cambiar rol de un usuario (solo admin)")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Rol cambiado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado") })
    public ResponseEntity<UsuarioDto> cambiarRolUsuario(@PathVariable String usuarioId,
            @RequestBody CambiarRolRequest request, @RequestHeader("Authorization") String authHeader) {

        // Validar que el usuario es admin
        if (!esUsuarioAdmin(authHeader)) {
            return ResponseEntity.status(403).build();
        }

        Usuario usuario = usuarioService.cambiarRol(usuarioId, request.nuevoRol());
        return ResponseEntity.ok(convertirADto(usuario));
    }

    private boolean esUsuarioAdmin(String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            return sessionService.validate(token).map(usuario -> usuario.tieneRol(RolUsuario.ADMIN)).orElse(false);
        } catch (Exception e) {
            return false;
        }
    }

    private UsuarioDto convertirADto(Usuario usuario) {
        return new UsuarioDto(usuario.getId(), usuario.getEmail(), usuario.getRoles().iterator().next(), // Asumimos un
                                                                                                         // rol por
                                                                                                         // usuario
                usuario.getFechaCreacion());
    }
}