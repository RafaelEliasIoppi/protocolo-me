package back.backend.dto;

public class ProtocoloResumoDTO {
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

    public ProtocoloResumoDTO() {
    }

    public ProtocoloResumoDTO(Long id, String numeroProtocolo, String status, String hospitalOrigem,
            String diagnosticoBasico) {
        this.id = id;
        this.numeroProtocolo = numeroProtocolo;
        this.status = status;
        this.hospitalOrigem = hospitalOrigem;
        this.diagnosticoBasico = diagnosticoBasico;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHospitalOrigem() {
        return hospitalOrigem;
    }

    public void setHospitalOrigem(String hospitalOrigem) {
        this.hospitalOrigem = hospitalOrigem;
    }

    public String getDiagnosticoBasico() {
        return diagnosticoBasico;
    }

    public void setDiagnosticoBasico(String diagnosticoBasico) {
        this.diagnosticoBasico = diagnosticoBasico;
    }

    public String getCausaMorte() {
        return causaMorte;
    }

    public void setCausaMorte(String causaMorte) {
        this.causaMorte = causaMorte;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public String getMedicoResponsavel() {
        return medicoResponsavel;
    }

    public void setMedicoResponsavel(String medicoResponsavel) {
        this.medicoResponsavel = medicoResponsavel;
    }

    public String getEnfermeiro() {
        return enfermeiro;
    }

    public void setEnfermeiro(String enfermeiro) {
        this.enfermeiro = enfermeiro;
    }

    public String getOrgaosDisponiveis() {
        return orgaosDisponiveis;
    }

    public void setOrgaosDisponiveis(String orgaosDisponiveis) {
        this.orgaosDisponiveis = orgaosDisponiveis;
    }
}
