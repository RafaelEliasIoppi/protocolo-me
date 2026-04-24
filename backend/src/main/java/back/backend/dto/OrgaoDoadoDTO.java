package back.backend.dto;

import back.backend.model.OrgaoDoado;

import java.time.LocalDateTime;

public class OrgaoDoadoDTO {

    private Long id;

    private Long protocoloId;
    private String numeroProtocolo;

    private String nomeOrgao;
    private String status;

    private String motivo;
    private String hospitalReceptor;
    private String pacienteReceptor;
    private String cpfReceptor;

    private LocalDateTime dataArmazenamento;
    private LocalDateTime dataImplantacao;
    private LocalDateTime dataDescarte;

    private String motivoDescarte;
    private String observacoes;

    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public OrgaoDoadoDTO() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProtocoloId() { return protocoloId; }
    public void setProtocoloId(Long protocoloId) { this.protocoloId = protocoloId; }

    public String getNumeroProtocolo() { return numeroProtocolo; }
    public void setNumeroProtocolo(String numeroProtocolo) { this.numeroProtocolo = numeroProtocolo; }

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

    public LocalDateTime getDataArmazenamento() { return dataArmazenamento; }
    public void setDataArmazenamento(LocalDateTime dataArmazenamento) { this.dataArmazenamento = dataArmazenamento; }

    public LocalDateTime getDataImplantacao() { return dataImplantacao; }
    public void setDataImplantacao(LocalDateTime dataImplantacao) { this.dataImplantacao = dataImplantacao; }

    public LocalDateTime getDataDescarte() { return dataDescarte; }
    public void setDataDescarte(LocalDateTime dataDescarte) { this.dataDescarte = dataDescarte; }

    public String getMotivoDescarte() { return motivoDescarte; }
    public void setMotivoDescarte(String motivoDescarte) { this.motivoDescarte = motivoDescarte; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }

    public static OrgaoDoadoDTO fromEntity(OrgaoDoado entity) {
        if (entity == null) return null;

        Long protocoloId = null;
        String numeroProtocolo = null;

        if (entity.getProtocoloME() != null) {
            protocoloId = entity.getProtocoloME().getId();
            numeroProtocolo = entity.getProtocoloME().getNumeroProtocolo();
        }

        OrgaoDoadoDTO dto = new OrgaoDoadoDTO();
        dto.setId(entity.getId());
        dto.setProtocoloId(protocoloId);
        dto.setNumeroProtocolo(numeroProtocolo);
        dto.setNomeOrgao(entity.getNomeOrgao());
        dto.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);
        dto.setMotivo(entity.getMotivo());
        dto.setHospitalReceptor(entity.getHospitalReceptor());
        dto.setPacienteReceptor(entity.getPacienteReceptor());
        dto.setCpfReceptor(entity.getCpfReceptor());
        dto.setDataArmazenamento(entity.getDataArmazenamento());
        dto.setDataImplantacao(entity.getDataImplantacao());
        dto.setDataDescarte(entity.getDataDescarte());
        dto.setMotivoDescarte(entity.getMotivoDescarte());
        dto.setObservacoes(entity.getObservacoes());
        dto.setDataCriacao(entity.getDataCriacao());
        dto.setDataAtualizacao(entity.getDataAtualizacao());
        return dto;
    }
}