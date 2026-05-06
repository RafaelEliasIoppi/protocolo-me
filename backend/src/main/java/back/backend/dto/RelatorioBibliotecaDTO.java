package back.backend.dto;

import java.time.LocalDateTime;

public class RelatorioBibliotecaDTO {

    private Long pacienteId;
    private String nomePaciente;
    private String cpf;

    private Long protocoloId;
    private String numeroProtocolo;
    private LocalDateTime dataAtualizacao;
    private String relatorioFinalEditavel;

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    public String getNomePaciente() {
        return nomePaciente;
    }

    public void setNomePaciente(String nomePaciente) {
        this.nomePaciente = nomePaciente;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Long getProtocoloId() {
        return protocoloId;
    }

    public void setProtocoloId(Long protocoloId) {
        this.protocoloId = protocoloId;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public String getRelatorioFinalEditavel() {
        return relatorioFinalEditavel;
    }

    public void setRelatorioFinalEditavel(String relatorioFinalEditavel) {
        this.relatorioFinalEditavel = relatorioFinalEditavel;
    }
}
