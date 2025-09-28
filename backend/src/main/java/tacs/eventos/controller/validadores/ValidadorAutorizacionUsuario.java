package tacs.eventos.controller.validadores;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;

import tacs.eventos.model.RolUsuario;
import tacs.eventos.model.Usuario;

/**
 * @param autenticado
 *            usuario autenticado en el sistema
 * @param autorizados
 *            usuarios con permiso para realizar la acción
 */
public class ValidadorAutorizacionUsuario implements Validador {
    private Usuario autenticado;
    private List<Usuario> autorizados;
    private HttpStatus errorARetornar;

    public ValidadorAutorizacionUsuario(Usuario autenticado, Usuario autorizado) {
        this.autenticado = autenticado;
        this.autorizados = Arrays.asList(autorizado);
    }

    public ValidadorAutorizacionUsuario(Usuario autenticado, List<Usuario> autorizados) {
        this.autenticado = autenticado;
        this.autorizados = autorizados;
    }

    /**
     * Verifica que el usuario autenticado esté dentro de los autorizados para la acción que quiere realizar.
     * <p>
     * Si no lo está, lanza una excepción con el código 403 FORBIDDEN, o 404 NOT_FOUND si se indicó así en el parámetro
     * correspondiente. Esto último es útil para no revelar información sensible, como por ejemplo si un usuario existe,
     * o si está inscrito a un evento.
     *
     */
    @Override
    public boolean validar() {
        return estaEntreLosAutorizados(this.autenticado, this.autorizados);
    }

    private boolean estaEntreLosAutorizados(Usuario autenticado, List<Usuario> autorizados) {
        // Los ADMIN tienen acceso a todo
        if (autenticado.getRoles().contains(RolUsuario.ADMIN)) {
            return true;
        }

        return autorizados.stream().anyMatch(autenticado::equals);
    }
}
