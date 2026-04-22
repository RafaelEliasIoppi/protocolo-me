package back.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

// Classe para padronizar respostas de erro
class ErroResponse {
    private String mensagem;
    private int codigo;
    private Map<String, String> detalhes;

    public ErroResponse(String mensagem, int codigo) {
        this.mensagem = mensagem;
        this.codigo = codigo;
    }

    public ErroResponse(String mensagem, int codigo, Map<String, String> detalhes) {
        this.mensagem = mensagem;
        this.codigo = codigo;
        this.detalhes = detalhes;
    }

    public String getMensagem() { return mensagem; }
    public int getCodigo() { return codigo; }
    public Map<String, String> getDetalhes() { return detalhes; }
}

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Captura erros de validação (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(new ErroResponse("Erro de validação", 400, errors), HttpStatus.BAD_REQUEST);
    }

    // Captura exceções genéricas de negócio
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErroResponse> handleRuntimeException(RuntimeException ex) {
        return new ResponseEntity<>(new ErroResponse(ex.getMessage(), 400), HttpStatus.BAD_REQUEST);
    }

    // Captura argumentos inválidos (ex: status inválido)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(new ErroResponse("Parâmetro inválido: " + ex.getMessage(), 400), HttpStatus.BAD_REQUEST);
    }
}
