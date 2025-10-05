package tacs.eventos.controller.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import tacs.eventos.controller.error.handlers.AccesoDenegadoHandler;
import tacs.eventos.controller.error.handlers.AccesoNoAutorizadoHandler;
import tacs.eventos.controller.error.handlers.RecursoNoEncontradoHandler;
import tacs.eventos.dto.errores.ErrorResponse;

@RestControllerAdvice
public class ManejadorDeExcepciones {
    @ExceptionHandler(AccesoNoAutorizadoHandler.class)
    public ResponseEntity<ErrorResponse> handleAccesoNoAutorizado(RecursoNoEncontradoHandler ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AccesoDenegadoHandler.class)
    public ResponseEntity<ErrorResponse> handleAccesoDenegado(RecursoNoEncontradoHandler ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN.value());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(RecursoNoEncontradoHandler.class)
    public ResponseEntity<ErrorResponse> handleRecursoNoEncontrado(RecursoNoEncontradoHandler ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleExcepcionGeneral(Exception ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
