package back.backend.dto;

public class ProtocoloSemEstatisticaDTO {

    private Long protocoloMEId;
    private String numeroProtocolo;
    private String nomeDoador;
    private Integer ano;

    public Long getProtocoloMEId() {
        return protocoloMEId;
    }

    public void setProtocoloMEId(Long protocoloMEId) {
        this.protocoloMEId = protocoloMEId;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public String getNomeDoador() {
        return nomeDoador;
    }

    public void setNomeDoador(String nomeDoador) {
        this.nomeDoador = nomeDoador;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }
}
