package back.backend.dto;

import java.util.Map;

public class ErrorResponseDTO {

    private String mensagem;
    private int codigo;
    private Map<String, String> detalhes;
    private java.time.LocalDateTime dataHora;

    public ErrorResponseDTO() {
    }

    public ErrorResponseDTO(String mensagem, int codigo) {
        this.mensagem = mensagem;
        this.codigo = codigo;
    }

    public ErrorResponseDTO(String mensagem, int codigo, Map<String, String> detalhes) {
        this.mensagem = mensagem;
        this.codigo = codigo;
        this.detalhes = detalhes;
    }

    public ErrorResponseDTO(String mensagem, int codigo, Map<String, String> detalhes, java.time.LocalDateTime dataHora) {
        this.mensagem = mensagem;
        this.codigo = codigo;
        this.detalhes = detalhes;
        this.dataHora = dataHora;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public Map<String, String> getDetalhes() {
        return detalhes;
    }

    public void setDetalhes(Map<String, String> detalhes) {
        this.detalhes = detalhes;
    }

    public java.time.LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(java.time.LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
}