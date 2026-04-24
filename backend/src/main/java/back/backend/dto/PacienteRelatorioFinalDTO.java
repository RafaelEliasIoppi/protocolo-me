package back.backend.dto;

import java.util.ArrayList;
import java.util.List;

public class PacienteRelatorioFinalDTO {

    private Long pacienteId;
    private String nomePaciente;
    private String cpf;
    private String hospital;
    private String statusPaciente;
    private String statusEntrevistaFamiliar;
    private String statusFinalProtocolo;
    private String conclusaoFinal;
    private Integer totalProtocolos;
    private List<ProtocoloRelatorioResumoDTO> protocolos = new ArrayList<>();

    public static class ProtocoloRelatorioResumoDTO {
        private Long protocoloId;
        private String numeroProtocolo;
        private String statusProtocolo;
        private String dataNotificacao;
        private String dataConfirmacaoME;
        private Integer totalExames;
        private Integer examesRealizados;
        private Integer examesPendentes;
        private Integer examesClinicosRealizados;
        private Integer examesComplementaresRealizados;
        private Integer examesLaboratoriaisRealizados;
        private Boolean familiaNotificada;
        private Boolean autopsiaAutorizada;
        private String relatorioFinalEditavel;
        private List<String> anexos = new ArrayList<>();

        public Long getProtocoloId() { return protocoloId; }
        public void setProtocoloId(Long protocoloId) { this.protocoloId = protocoloId; }

        public String getNumeroProtocolo() { return numeroProtocolo; }
        public void setNumeroProtocolo(String numeroProtocolo) { this.numeroProtocolo = numeroProtocolo; }

        public String getStatusProtocolo() { return statusProtocolo; }
        public void setStatusProtocolo(String statusProtocolo) { this.statusProtocolo = statusProtocolo; }

        public String getDataNotificacao() { return dataNotificacao; }
        public void setDataNotificacao(String dataNotificacao) { this.dataNotificacao = dataNotificacao; }

        public String getDataConfirmacaoME() { return dataConfirmacaoME; }
        public void setDataConfirmacaoME(String dataConfirmacaoME) { this.dataConfirmacaoME = dataConfirmacaoME; }

        public Integer getTotalExames() { return totalExames; }
        public void setTotalExames(Integer totalExames) { this.totalExames = totalExames; }

        public Integer getExamesRealizados() { return examesRealizados; }
        public void setExamesRealizados(Integer examesRealizados) { this.examesRealizados = examesRealizados; }

        public Integer getExamesPendentes() { return examesPendentes; }
        public void setExamesPendentes(Integer examesPendentes) { this.examesPendentes = examesPendentes; }

        public Integer getExamesClinicosRealizados() { return examesClinicosRealizados; }
        public void setExamesClinicosRealizados(Integer examesClinicosRealizados) { this.examesClinicosRealizados = examesClinicosRealizados; }

        public Integer getExamesComplementaresRealizados() { return examesComplementaresRealizados; }
        public void setExamesComplementaresRealizados(Integer examesComplementaresRealizados) { this.examesComplementaresRealizados = examesComplementaresRealizados; }

        public Integer getExamesLaboratoriaisRealizados() { return examesLaboratoriaisRealizados; }
        public void setExamesLaboratoriaisRealizados(Integer examesLaboratoriaisRealizados) { this.examesLaboratoriaisRealizados = examesLaboratoriaisRealizados; }

        public Boolean getFamiliaNotificada() { return familiaNotificada; }
        public void setFamiliaNotificada(Boolean familiaNotificada) { this.familiaNotificada = familiaNotificada; }

        public Boolean getAutopsiaAutorizada() { return autopsiaAutorizada; }
        public void setAutopsiaAutorizada(Boolean autopsiaAutorizada) { this.autopsiaAutorizada = autopsiaAutorizada; }

        public String getRelatorioFinalEditavel() { return relatorioFinalEditavel; }
        public void setRelatorioFinalEditavel(String relatorioFinalEditavel) { this.relatorioFinalEditavel = relatorioFinalEditavel; }

        public List<String> getAnexos() { return anexos; }
        public void setAnexos(List<String> anexos) { this.anexos = anexos; }
    }

    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }

    public String getNomePaciente() { return nomePaciente; }
    public void setNomePaciente(String nomePaciente) { this.nomePaciente = nomePaciente; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getHospital() { return hospital; }
    public void setHospital(String hospital) { this.hospital = hospital; }

    public String getStatusPaciente() { return statusPaciente; }
    public void setStatusPaciente(String statusPaciente) { this.statusPaciente = statusPaciente; }

    public String getStatusEntrevistaFamiliar() { return statusEntrevistaFamiliar; }
    public void setStatusEntrevistaFamiliar(String statusEntrevistaFamiliar) { this.statusEntrevistaFamiliar = statusEntrevistaFamiliar; }

    public String getStatusFinalProtocolo() { return statusFinalProtocolo; }
    public void setStatusFinalProtocolo(String statusFinalProtocolo) { this.statusFinalProtocolo = statusFinalProtocolo; }

    public String getConclusaoFinal() { return conclusaoFinal; }
    public void setConclusaoFinal(String conclusaoFinal) { this.conclusaoFinal = conclusaoFinal; }

    public Integer getTotalProtocolos() { return totalProtocolos; }
    public void setTotalProtocolos(Integer totalProtocolos) { this.totalProtocolos = totalProtocolos; }

    public List<ProtocoloRelatorioResumoDTO> getProtocolos() { return protocolos; }
    public void setProtocolos(List<ProtocoloRelatorioResumoDTO> protocolos) { this.protocolos = protocolos; }
}
