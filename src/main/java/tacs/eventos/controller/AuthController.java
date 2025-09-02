package tacs.eventos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tacs.eventos.dto.LoginRequest;
import tacs.eventos.dto.RegistroRequest;
import tacs.eventos.dto.SessionResponse;
import tacs.eventos.dto.UsuarioResponse;
import tacs.eventos.model.Usuario;
import tacs.eventos.service.SessionService;
import tacs.eventos.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UsuarioService usuarios;
    private final SessionService sesiones;

    public AuthController(UsuarioService usuarios, SessionService sesiones) {
        this.usuarios = usuarios;
        this.sesiones = sesiones;
    }

    @PostMapping("/register")
    @Operation(summary = "Registra un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "El usuario se registro satifactoriamente"),
            @ApiResponse(responseCode = "401", description = "No se pudo regisrar al usuario"),
    })
    public ResponseEntity<SessionResponse> register(@Valid @RequestBody RegistroRequest req) {
        usuarios.registrar(req.email(), req.password());
        return sesiones.login(req.email(), req.password())
                .map(s -> ResponseEntity.ok(new SessionResponse(s.getToken(), s.getExpiresAt())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/login")
    @Operation(summary = "Login que devuelve token de sesión y expiración")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se pudo genera token de sesión"),
            @ApiResponse(responseCode = "401", description = "No se pudo regisrar al usuario"),
    })
    public ResponseEntity<SessionResponse> login(@Valid @RequestBody LoginRequest req) {
        return sesiones.login(req.email(), req.password())
                .map(s -> ResponseEntity.ok(new SessionResponse(s.getToken(), s.getExpiresAt())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/logout")
    @Operation(summary = "Invalidar el token de sesión actual (enviar por header o body)")
    public ResponseEntity<Void> logout(@RequestHeader(value = "X-Session-Token", required = false) String token,
            @RequestBody(required = false) SessionResponse body) {
        String t = token != null ? token : (body != null ? body.token() : null);
        if (t != null)
            sesiones.logout(t);
        System.out.println("Logout realizado con token: " + t);
        return ResponseEntity.noContent().build();
    }
}
