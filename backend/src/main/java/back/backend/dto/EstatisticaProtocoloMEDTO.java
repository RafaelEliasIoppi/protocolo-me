package back.backend.dto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class EstatisticaProtocoloMEDTO {

    private Long id;
    private Long protocoloMEId;
    private String numeroProtocolo;
    private Long pacienteId;
    private String nomeDoador;
    private Integer anoCompetencia;
    private Integer mesCompetencia;
    private String periodicidade;
    private Map<String, String> campos = new HashMap<>();
    private String atualizadoPor;
    private LocalDateTime dataAtualizacao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    public String getNomeDoador() {
        return nomeDoador;
    }

    public void setNomeDoador(String nomeDoador) {
        this.nomeDoador = nomeDoador;
    }

    public Integer getAnoCompetencia() {
        return anoCompetencia;
    }

    public void setAnoCompetencia(Integer anoCompetencia) {
        this.anoCompetencia = anoCompetencia;
    }

    public Integer getMesCompetencia() {
        return mesCompetencia;
    }

    public void setMesCompetencia(Integer mesCompetencia) {
        this.mesCompetencia = mesCompetencia;
    }

    public String getPeriodicidade() {
        return periodicidade;
    }

    public void setPeriodicidade(String periodicidade) {
        this.periodicidade = periodicidade;
    }

    public Map<String, String> getCampos() {
        return campos;
    }

    public void setCampos(Map<String, String> campos) {
        this.campos = campos;
    }

    public String getAtualizadoPor() {
        return atualizadoPor;
    }

    public void setAtualizadoPor(String atualizadoPor) {
        this.atualizadoPor = atualizadoPor;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }
}
