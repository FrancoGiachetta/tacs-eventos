package tacs.eventos.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tacs.eventos.dto.errores.Error;
import tacs.eventos.dto.errores.ErrorResponse;

@RestControllerAdvice
public class ManejadorExcepciones {

    /*
     * TODO: creo que estaría bueno unificar los formatos de respuesta del servidor ante los errores de todos los tipos,
     * y hacer métodos handlers de otros tipos de errores que devuelvan este mismo tipo de response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationErrors(MethodArgumentNotValidException excepcion) {
        return new ErrorResponse(excepcion.getBindingResult().getFieldErrors().stream()
                .map(err -> new Error(err.getField(), err.getDefaultMessage())).toList());
    }
}
