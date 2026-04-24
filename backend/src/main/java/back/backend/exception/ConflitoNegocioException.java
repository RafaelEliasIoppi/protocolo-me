package back.backend.exception;

public class ConflitoNegocioException extends RuntimeException {
    public ConflitoNegocioException(String mensagem) {
        super(mensagem);
    }

    public ConflitoNegocioException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}