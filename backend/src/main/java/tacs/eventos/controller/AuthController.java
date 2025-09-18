package tacs.eventos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "El usuario se registro satifactoriamente"),
            @ApiResponse(responseCode = "401", description = "No se pudo regisrar al usuario"), })
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
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Se pudo genera token de sesión"),
            @ApiResponse(responseCode = "401", description = "No se pudo regisrar al usuario"), })
    public ResponseEntity<SessionResponse> login(@Valid @RequestBody LoginRequest req) {
        System.out.println("Login attempt for " + req.email());

        return sesiones.login(req.email(), req.password()).map(s -> {
            System.out.println("Login successful for " + req.email() + ", token: " + s.getToken());
            return ResponseEntity.ok(new SessionResponse(s.getToken(), s.getExpiresAt()));
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    /**
     * Cierra la sesión de un usuario.
     *
     * @param authorization
     *            token en header Authorization: Bearer (token)
     *
     * @param xSessionToken
     *            token en header X-Session-Token
     *
     * @param body
     *            información sobre la sesión a cerrar. Contiene el token y tiempo de expiración.
     */
    @PostMapping("/logout")
    @Operation(summary = "Invalidar el token de sesión actual (enviar por header o body)")
    public ResponseEntity<Void> logout(@RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestHeader(value = "X-Session-Token", required = false) String xSessionToken,
            @RequestBody(required = false) SessionResponse body) {
        String t = extractSessionToken(authorization, xSessionToken, body);

        if (t != null) {
            sesiones.logout(t);
        }
        // hacer logout idempotente
        return ResponseEntity.noContent().build();
    }

    private static String extractSessionToken(String authHeader, String xHeader, SessionResponse body) {
        if (authHeader != null) {
            String prefix = "Bearer ";
            if (authHeader.regionMatches(true, 0, prefix, 0, prefix.length())) {
                String candidate = authHeader.substring(prefix.length()).trim();
                if (!candidate.isEmpty())
                    return candidate;
            }
        }
        if (xHeader != null && !xHeader.isBlank()) {
            return xHeader.trim();
        }
        if (body != null && body.token() != null && !body.token().isBlank()) {
            return body.token().trim();
        }
        return null;
    }
}
