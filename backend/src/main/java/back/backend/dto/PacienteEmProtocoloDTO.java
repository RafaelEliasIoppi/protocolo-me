package back.backend.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PacienteEmProtocoloDTO {

    private Long id;
    private String nome;
    private String cpf;
    private LocalDate dataNascimento;
    private String genero;
    private HospitalResumoDTO hospital;
    private String leito;
    private LocalDate dataInternacao;
    private String status;
    private String statusEntrevistaFamiliar;
    private String diagnosticoPrincipal;
    private List<ProtocoloResumoDTO> protocolosME = new ArrayList<>();

    public static class HospitalResumoDTO {
        private Long id;
        private String nomeHospital;

        public HospitalResumoDTO() {
        }

        public HospitalResumoDTO(Long id, String nomeHospital) {
            this.id = id;
            this.nomeHospital = nomeHospital;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getNomeHospital() { return nomeHospital; }
        public void setNomeHospital(String nomeHospital) { this.nomeHospital = nomeHospital; }
    }

    public static class ProtocoloResumoDTO {
        private Long id;
        private String numeroProtocolo;
        private String status;
        private String hospitalOrigem;
        private String diagnosticoBasico;
        private String causaMorte;
        private String observacoes;
        private String medicoResponsavel;
        private String enfermeiro;
        private String orgaosDisponiveis;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getNumeroProtocolo() { return numeroProtocolo; }
        public void setNumeroProtocolo(String numeroProtocolo) { this.numeroProtocolo = numeroProtocolo; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getHospitalOrigem() { return hospitalOrigem; }
        public void setHospitalOrigem(String hospitalOrigem) { this.hospitalOrigem = hospitalOrigem; }

        public String getDiagnosticoBasico() { return diagnosticoBasico; }
        public void setDiagnosticoBasico(String diagnosticoBasico) { this.diagnosticoBasico = diagnosticoBasico; }

        public String getCausaMorte() { return causaMorte; }
        public void setCausaMorte(String causaMorte) { this.causaMorte = causaMorte; }

        public String getObservacoes() { return observacoes; }
        public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

        public String getMedicoResponsavel() { return medicoResponsavel; }
        public void setMedicoResponsavel(String medicoResponsavel) { this.medicoResponsavel = medicoResponsavel; }

        public String getEnfermeiro() { return enfermeiro; }
        public void setEnfermeiro(String enfermeiro) { this.enfermeiro = enfermeiro; }

        public String getOrgaosDisponiveis() { return orgaosDisponiveis; }
        public void setOrgaosDisponiveis(String orgaosDisponiveis) { this.orgaosDisponiveis = orgaosDisponiveis; }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public HospitalResumoDTO getHospital() { return hospital; }
    public void setHospital(HospitalResumoDTO hospital) { this.hospital = hospital; }

    public String getLeito() { return leito; }
    public void setLeito(String leito) { this.leito = leito; }

    public LocalDate getDataInternacao() { return dataInternacao; }
    public void setDataInternacao(LocalDate dataInternacao) { this.dataInternacao = dataInternacao; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getStatusEntrevistaFamiliar() { return statusEntrevistaFamiliar; }
    public void setStatusEntrevistaFamiliar(String statusEntrevistaFamiliar) { this.statusEntrevistaFamiliar = statusEntrevistaFamiliar; }

    public String getDiagnosticoPrincipal() { return diagnosticoPrincipal; }
    public void setDiagnosticoPrincipal(String diagnosticoPrincipal) { this.diagnosticoPrincipal = diagnosticoPrincipal; }

    public List<ProtocoloResumoDTO> getProtocolosME() { return protocolosME; }
    public void setProtocolosME(List<ProtocoloResumoDTO> protocolosME) { this.protocolosME = protocolosME; }
}
