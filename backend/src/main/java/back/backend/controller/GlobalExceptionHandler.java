package back.backend.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import back.backend.dto.ErrorResponseDTO;
import back.backend.exception.AutenticacaoException;
import back.backend.exception.ConflitoNegocioException;
import back.backend.exception.RecursoNaoEncontradoException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // =========================
    // 404 - NOT FOUND
    // =========================
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(RecursoNaoEncontradoException ex) {

        log.warn("Recurso não encontrado: {}", ex.getMessage());

        return buildResponse(
                ex.getMessage(),
                HttpStatus.NOT_FOUND,
                null);
    }

    // =========================
    // 400 - VALIDATION (@Valid)
    // =========================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidation(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            String field = error instanceof FieldError ? ((FieldError) error).getField() : error.getObjectName();
            errors.put(field, error.getDefaultMessage());
        }

        log.warn("Erro de validação: {}", errors);

        return buildResponse(
                "Erro de validação",
                HttpStatus.BAD_REQUEST,
                errors);
    }

    // =========================
    // 400 - ILLEGAL ARGUMENT
    // =========================
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgument(IllegalArgumentException ex) {

        log.warn("Parâmetro inválido: {}", ex.getMessage());

        return buildResponse(
                "Parâmetro inválido: " + ex.getMessage(),
                HttpStatus.BAD_REQUEST,
                null);
    }

    // =========================
    // 401 - AUTHENTICATION
    // =========================
    @ExceptionHandler(AutenticacaoException.class)
    public ResponseEntity<ErrorResponseDTO> handleAutenticacao(AutenticacaoException ex) {

        log.warn("Falha de autenticação: {}", ex.getMessage());

        return buildResponse(
                ex.getMessage(),
                HttpStatus.UNAUTHORIZED,
                null);
    }

    // =========================
    // 400 - BUSINESS RULE
    // =========================
    @ExceptionHandler({ ConflitoNegocioException.class, IllegalStateException.class })
    public ResponseEntity<ErrorResponseDTO> handleBusiness(RuntimeException ex) {

        log.warn("Erro de regra de negócio: {}", ex.getMessage());

        return buildResponse(
                ex.getMessage(),
                HttpStatus.CONFLICT,
                null);
    }

    // =========================
    // 400 - MALFORMED JSON
    // =========================
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleMalformedJson(HttpMessageNotReadableException ex) {

        log.warn("Payload JSON inválido: {}", ex.getMessage());

        return buildResponse(
                "Payload inválido",
                HttpStatus.BAD_REQUEST,
                null);
    }

    // =========================
    // 500 - GENERIC (FALLBACK)
    // =========================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneric(Exception ex) {

        log.error("Erro inesperado no sistema", ex);

        return buildResponse(
                "Erro interno no servidor",
                HttpStatus.INTERNAL_SERVER_ERROR,
                null);
    }

    // =========================
    // BUILDER CENTRALIZADO
    // =========================
    private ResponseEntity<ErrorResponseDTO> buildResponse(
            String mensagem,
            HttpStatus status,
            Map<String, String> detalhes) {
        ErrorResponseDTO body = new ErrorResponseDTO(
                mensagem,
                status.value(),
                detalhes,
                LocalDateTime.now());

        return ResponseEntity.status(status).body(body);
    }
}
