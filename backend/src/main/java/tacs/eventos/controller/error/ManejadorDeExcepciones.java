package tacs.eventos.controller.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import tacs.eventos.controller.error.handlers.AccesoDenegadoHandler;
import tacs.eventos.controller.error.handlers.AccesoNoAutorizadoHandler;
import tacs.eventos.controller.error.handlers.ErrorInternoHandler;
import tacs.eventos.controller.error.handlers.RecursoNoEncontradoHandler;
import tacs.eventos.dto.errores.ErrorResponse;

@RestControllerAdvice
public class ManejadorDeExcepciones {

    @ExceptionHandler(AccesoNoAutorizadoHandler.class)
    public ResponseEntity<ErrorResponse> handleAccesoNoAutorizado(AccesoNoAutorizadoHandler ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED.value());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(AccesoDenegadoHandler.class)
    public ResponseEntity<ErrorResponse> handleAccesoDenegado(AccesoDenegadoHandler ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN.value());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(RecursoNoEncontradoHandler.class)
    public ResponseEntity<ErrorResponse> handleRecursoNoEncontrado(RecursoNoEncontradoHandler ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ErrorInternoHandler.class)
    public ResponseEntity<ErrorResponse> handleErrorInterno(ErrorInternoHandler ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleExcepcionGeneral(Exception ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
