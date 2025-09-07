package tacs.eventos.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tacs.eventos.dto.LoginRequest;
import tacs.eventos.dto.RegistroRequest;
import tacs.eventos.dto.SessionResponse;
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

    /**
     * Registra un nuevo usuario.
     *
     * @param req
     *            datos de registro de sesión. Contiene el email y la contraseña.
     *
     * @return el token de la sesión y su tiempo de expieración.
     */
    @PostMapping("/register")
    @Operation(summary = "Registra un usuario")
    public ResponseEntity<SessionResponse> register(@Valid @RequestBody RegistroRequest req) {
        usuarios.registrar(req.email(), req.password());
        return sesiones.login(req.email(), req.password())
                .map(s -> ResponseEntity.ok(new SessionResponse(s.getToken(), s.getExpiresAt())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    /**
     * Loguea a un usuario según sus datos de sesion.
     *
     * @param req
     *            datos de inicio de sesión. Contiene el email y la contraseña.
     *
     * @return el token de la sesión y su tiempo de expieración. Si alguno de los tados no es correcto, retorna 401.
     */
    @PostMapping("/login")
    @Operation(summary = "Login que devuelve token de sesión y expiración")
    public ResponseEntity<SessionResponse> login(@Valid @RequestBody LoginRequest req) {
        return sesiones.login(req.email(), req.password())
                .map(s -> ResponseEntity.ok(new SessionResponse(s.getToken(), s.getExpiresAt())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    /**
     * Cierra la sesión de un usuario.
     *
     * @param token
     *            token asociado a una sesión.
     * @param body
     *            información sobre la sesión a cerrar. Contiene el token y tiempo de expiración.
     */
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
