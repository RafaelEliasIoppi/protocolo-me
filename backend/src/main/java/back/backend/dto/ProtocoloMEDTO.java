package back.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ProtocoloMEDTO {

    public static class PacienteResumoDTO {
        private Long id;
        private String nome;
        private String cpf;
        private Long hospitalId;
        private String hospitalNome;
        private String leito;
        private String statusEntrevistaFamiliar;

        public PacienteResumoDTO() {
        }

        public PacienteResumoDTO(Long id,
                                 String nome,
                                 String cpf,
                                 Long hospitalId,
                                 String hospitalNome,
                                 String leito,
                                 String statusEntrevistaFamiliar) {
            this.id = id;
            this.nome = nome;
            this.cpf = cpf;
            this.hospitalId = hospitalId;
            this.hospitalNome = hospitalNome;
            this.leito = leito;
            this.statusEntrevistaFamiliar = statusEntrevistaFamiliar;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }

        public String getCpf() { return cpf; }
        public void setCpf(String cpf) { this.cpf = cpf; }

        public Long getHospitalId() { return hospitalId; }
        public void setHospitalId(Long hospitalId) { this.hospitalId = hospitalId; }

        public String getHospitalNome() { return hospitalNome; }
        public void setHospitalNome(String hospitalNome) { this.hospitalNome = hospitalNome; }

        public String getLeito() { return leito; }
        public void setLeito(String leito) { this.leito = leito; }

        public String getStatusEntrevistaFamiliar() { return statusEntrevistaFamiliar; }
        public void setStatusEntrevistaFamiliar(String statusEntrevistaFamiliar) { this.statusEntrevistaFamiliar = statusEntrevistaFamiliar; }
    }

    private Long id;
    private String numeroProtocolo;
    private String hospitalOrigem;
    private String medicoResponsavel;
    private String enfermeiro;
    private String status;
    private String diagnosticoBasico;
    private String causaMorte;
    private String observacoes;

    private Boolean testeClinico1Realizado;
    private LocalDateTime dataTesteClinico1;

    private Boolean testeClinico2Realizado;
    private LocalDateTime dataTesteClinico2;

    private Boolean testesComplementaresRealizados;
    private String testesComplementares;
    private LocalDateTime dataTesteComplementar;

    private Boolean familiaNotificada;
    private LocalDateTime dataNotificacaoFamilia;

    private Boolean autopsiaAutorizada;

    private String orgaosDisponiveis;

    private Boolean preservacaoOrgaos;
    private LocalDateTime dataPreservacao;

    private LocalDateTime dataNotificacao;
    private LocalDateTime dataConfirmacaoME;
    private LocalDateTime dataSaidaHospital;

    private String relatorioFinalEditavel;
    private String relatorioFinalAtualizadoPor;
    private LocalDateTime relatorioFinalAtualizadoEm;

    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    private Long centralTransplantesId;
    private String centralTransplantesNome;

    private PacienteResumoDTO paciente;

    private List<OrgaoDoadoDTO> orgaosDoados;

    public ProtocoloMEDTO() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumeroProtocolo() { return numeroProtocolo; }
    public void setNumeroProtocolo(String numeroProtocolo) { this.numeroProtocolo = numeroProtocolo; }

    public String getHospitalOrigem() { return hospitalOrigem; }
    public void setHospitalOrigem(String hospitalOrigem) { this.hospitalOrigem = hospitalOrigem; }

    public String getMedicoResponsavel() { return medicoResponsavel; }
    public void setMedicoResponsavel(String medicoResponsavel) { this.medicoResponsavel = medicoResponsavel; }

    public String getEnfermeiro() { return enfermeiro; }
    public void setEnfermeiro(String enfermeiro) { this.enfermeiro = enfermeiro; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDiagnosticoBasico() { return diagnosticoBasico; }
    public void setDiagnosticoBasico(String diagnosticoBasico) { this.diagnosticoBasico = diagnosticoBasico; }

    public String getCausaMorte() { return causaMorte; }
    public void setCausaMorte(String causaMorte) { this.causaMorte = causaMorte; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public Boolean getTesteClinico1Realizado() { return testeClinico1Realizado; }
    public void setTesteClinico1Realizado(Boolean testeClinico1Realizado) { this.testeClinico1Realizado = testeClinico1Realizado; }

    public LocalDateTime getDataTesteClinico1() { return dataTesteClinico1; }
    public void setDataTesteClinico1(LocalDateTime dataTesteClinico1) { this.dataTesteClinico1 = dataTesteClinico1; }

    public Boolean getTesteClinico2Realizado() { return testeClinico2Realizado; }
    public void setTesteClinico2Realizado(Boolean testeClinico2Realizado) { this.testeClinico2Realizado = testeClinico2Realizado; }

    public LocalDateTime getDataTesteClinico2() { return dataTesteClinico2; }
    public void setDataTesteClinico2(LocalDateTime dataTesteClinico2) { this.dataTesteClinico2 = dataTesteClinico2; }

    public Boolean getTestesComplementaresRealizados() { return testesComplementaresRealizados; }
    public void setTestesComplementaresRealizados(Boolean testesComplementaresRealizados) { this.testesComplementaresRealizados = testesComplementaresRealizados; }

    public String getTestesComplementares() { return testesComplementares; }
    public void setTestesComplementares(String testesComplementares) { this.testesComplementares = testesComplementares; }

    public LocalDateTime getDataTesteComplementar() { return dataTesteComplementar; }
    public void setDataTesteComplementar(LocalDateTime dataTesteComplementar) { this.dataTesteComplementar = dataTesteComplementar; }

    public Boolean getFamiliaNotificada() { return familiaNotificada; }
    public void setFamiliaNotificada(Boolean familiaNotificada) { this.familiaNotificada = familiaNotificada; }

    public LocalDateTime getDataNotificacaoFamilia() { return dataNotificacaoFamilia; }
    public void setDataNotificacaoFamilia(LocalDateTime dataNotificacaoFamilia) { this.dataNotificacaoFamilia = dataNotificacaoFamilia; }

    public Boolean getAutopsiaAutorizada() { return autopsiaAutorizada; }
    public void setAutopsiaAutorizada(Boolean autopsiaAutorizada) { this.autopsiaAutorizada = autopsiaAutorizada; }

    public String getOrgaosDisponiveis() { return orgaosDisponiveis; }
    public void setOrgaosDisponiveis(String orgaosDisponiveis) { this.orgaosDisponiveis = orgaosDisponiveis; }

    public Boolean getPreservacaoOrgaos() { return preservacaoOrgaos; }
    public void setPreservacaoOrgaos(Boolean preservacaoOrgaos) { this.preservacaoOrgaos = preservacaoOrgaos; }

    public LocalDateTime getDataPreservacao() { return dataPreservacao; }
    public void setDataPreservacao(LocalDateTime dataPreservacao) { this.dataPreservacao = dataPreservacao; }

    public LocalDateTime getDataNotificacao() { return dataNotificacao; }
    public void setDataNotificacao(LocalDateTime dataNotificacao) { this.dataNotificacao = dataNotificacao; }

    public LocalDateTime getDataConfirmacaoME() { return dataConfirmacaoME; }
    public void setDataConfirmacaoME(LocalDateTime dataConfirmacaoME) { this.dataConfirmacaoME = dataConfirmacaoME; }

    public LocalDateTime getDataSaidaHospital() { return dataSaidaHospital; }
    public void setDataSaidaHospital(LocalDateTime dataSaidaHospital) { this.dataSaidaHospital = dataSaidaHospital; }

    public String getRelatorioFinalEditavel() { return relatorioFinalEditavel; }
    public void setRelatorioFinalEditavel(String relatorioFinalEditavel) { this.relatorioFinalEditavel = relatorioFinalEditavel; }

    public String getRelatorioFinalAtualizadoPor() { return relatorioFinalAtualizadoPor; }
    public void setRelatorioFinalAtualizadoPor(String relatorioFinalAtualizadoPor) { this.relatorioFinalAtualizadoPor = relatorioFinalAtualizadoPor; }

    public LocalDateTime getRelatorioFinalAtualizadoEm() { return relatorioFinalAtualizadoEm; }
    public void setRelatorioFinalAtualizadoEm(LocalDateTime relatorioFinalAtualizadoEm) { this.relatorioFinalAtualizadoEm = relatorioFinalAtualizadoEm; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }

    public Long getCentralTransplantesId() { return centralTransplantesId; }
    public void setCentralTransplantesId(Long centralTransplantesId) { this.centralTransplantesId = centralTransplantesId; }

    public String getCentralTransplantesNome() { return centralTransplantesNome; }
    public void setCentralTransplantesNome(String centralTransplantesNome) { this.centralTransplantesNome = centralTransplantesNome; }

    public PacienteResumoDTO getPaciente() { return paciente; }
    public void setPaciente(PacienteResumoDTO paciente) { this.paciente = paciente; }

    public List<OrgaoDoadoDTO> getOrgaosDoados() { return orgaosDoados; }
    public void setOrgaosDoados(List<OrgaoDoadoDTO> orgaosDoados) { this.orgaosDoados = orgaosDoados; }
}