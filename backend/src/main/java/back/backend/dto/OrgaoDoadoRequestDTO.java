package back.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class OrgaoDoadoRequestDTO {

    private Long protocoloId;

    @NotBlank(message = "Nome do órgão é obrigatório")
    private String nomeOrgao;

    private String status;
    private String motivo;
    private String hospitalReceptor;
    private String pacienteReceptor;
    private String cpfReceptor;
    private String motivoDescarte;
    private String observacoes;

    public Long getProtocoloId() { return protocoloId; }
    public void setProtocoloId(Long protocoloId) { this.protocoloId = protocoloId; }

    public String getNomeOrgao() { return nomeOrgao; }
    public void setNomeOrgao(String nomeOrgao) { this.nomeOrgao = nomeOrgao; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getHospitalReceptor() { return hospitalReceptor; }
    public void setHospitalReceptor(String hospitalReceptor) { this.hospitalReceptor = hospitalReceptor; }

    public String getPacienteReceptor() { return pacienteReceptor; }
    public void setPacienteReceptor(String pacienteReceptor) { this.pacienteReceptor = pacienteReceptor; }

    public String getCpfReceptor() { return cpfReceptor; }
    public void setCpfReceptor(String cpfReceptor) { this.cpfReceptor = cpfReceptor; }

    public String getMotivoDescarte() { return motivoDescarte; }
    public void setMotivoDescarte(String motivoDescarte) { this.motivoDescarte = motivoDescarte; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}