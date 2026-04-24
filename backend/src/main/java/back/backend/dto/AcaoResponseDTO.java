package back.backend.dto;

public class AcaoResponseDTO {

    private Long id;
    private String mensagem;

    public AcaoResponseDTO() {
    }

    public AcaoResponseDTO(Long id, String mensagem) {
        this.id = id;
        this.mensagem = mensagem;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}