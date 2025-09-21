package tacs.eventos.controller.validadores;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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
    private boolean retornarNotFound;

    public ValidadorAutorizacionUsuario(Usuario autenticado, boolean retornarNotFound, Usuario autorizado) {
        this.autenticado = autenticado;
        this.autorizados = Arrays.asList(autorizado);
        this.retornarNotFound = retornarNotFound;
    }

    public ValidadorAutorizacionUsuario(Usuario autenticado, boolean retornarNotFound, List<Usuario> autorizados) {
        this.autenticado = autenticado;
        this.autorizados = autorizados;
        this.retornarNotFound = retornarNotFound;
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
    public void validar() {
        if (!estaEntreLosAutorizados(this.autenticado, this.autorizados))
            throw new ResponseStatusException(this.retornarNotFound ? HttpStatus.NOT_FOUND : HttpStatus.FORBIDDEN,
                    "El usuario no es organizador del evento");
    }

    private boolean estaEntreLosAutorizados(Usuario autenticado, List<Usuario> autorizados) {
        return autorizados.stream().anyMatch(autenticado::equals);
    }
}
