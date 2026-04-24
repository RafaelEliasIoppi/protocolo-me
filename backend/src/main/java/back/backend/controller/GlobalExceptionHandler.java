package back.backend.controller;

import back.backend.dto.ErrorResponseDTO;
import back.backend.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(RecursoNaoEncontradoException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage(), 404), HttpStatus.NOT_FOUND);
    }

    // Captura erros de validação (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(new ErrorResponseDTO("Erro de validação", 400, errors), HttpStatus.BAD_REQUEST);
    }

    // Captura exceções genéricas de negócio
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDTO> handleRuntimeException(RuntimeException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage(), 400), HttpStatus.BAD_REQUEST);
    }

    // Captura argumentos inválidos (ex: status inválido)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO("Parâmetro inválido: " + ex.getMessage(), 400), HttpStatus.BAD_REQUEST);
    }
}
