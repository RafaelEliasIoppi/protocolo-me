package back.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orgao_doado")
public class OrgaoDoado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "protocolo_me_id")
    @JsonIgnoreProperties({"exames", "orgaosDoados", "paciente", "centralTransplantes", "hibernateLazyInitializer", "handler"})
    private ProtocoloME protocoloME;

    @Column(nullable = false)
    private String nomeOrgao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusOrgaoDoado status = StatusOrgaoDoado.AGUARDANDO_IMPLANTACAO;

    @Column(length = 1000)
    private String motivo;

    @Column
    private String hospitalReceptor;

    @Column
    private String pacienteReceptor;

    @Column
    private String cpfReceptor;

    @Column
    private LocalDateTime dataArmazenamento;

    @Column
    private LocalDateTime dataImplantacao;

    @Column
    private LocalDateTime dataDescarte;

    @Column(length = 500)
    private String motivoDescarte;

    @Column(length = 1000)
    private String observacoes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column
    private LocalDateTime dataAtualizacao;

    // Construtores
    public OrgaoDoado() {}

    public OrgaoDoado(ProtocoloME protocoloME, String nomeOrgao) {
        this.protocoloME = protocoloME;
        this.nomeOrgao = nomeOrgao;
        this.status = StatusOrgaoDoado.AGUARDANDO_IMPLANTACAO;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProtocoloME getProtocoloME() {
        return protocoloME;
    }

    public void setProtocoloME(ProtocoloME protocoloME) {
        this.protocoloME = protocoloME;
    }

    public String getNomeOrgao() {
        return nomeOrgao;
    }

    public void setNomeOrgao(String nomeOrgao) {
        this.nomeOrgao = nomeOrgao;
    }

    public StatusOrgaoDoado getStatus() {
        return status;
    }

    public void setStatus(StatusOrgaoDoado status) {
        this.status = status;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getHospitalReceptor() {
        return hospitalReceptor;
    }

    public void setHospitalReceptor(String hospitalReceptor) {
        this.hospitalReceptor = hospitalReceptor;
    }

    public String getPacienteReceptor() {
        return pacienteReceptor;
    }

    public void setPacienteReceptor(String pacienteReceptor) {
        this.pacienteReceptor = pacienteReceptor;
    }

    public String getCpfReceptor() {
        return cpfReceptor;
    }

    public void setCpfReceptor(String cpfReceptor) {
        this.cpfReceptor = cpfReceptor;
    }

    public LocalDateTime getDataArmazenamento() {
        return dataArmazenamento;
    }

    public void setDataArmazenamento(LocalDateTime dataArmazenamento) {
        this.dataArmazenamento = dataArmazenamento;
    }

    public LocalDateTime getDataImplantacao() {
        return dataImplantacao;
    }

    public void setDataImplantacao(LocalDateTime dataImplantacao) {
        this.dataImplantacao = dataImplantacao;
    }

    public LocalDateTime getDataDescarte() {
        return dataDescarte;
    }

    public void setDataDescarte(LocalDateTime dataDescarte) {
        this.dataDescarte = dataDescarte;
    }

    public String getMotivoDescarte() {
        return motivoDescarte;
    }

    public void setMotivoDescarte(String motivoDescarte) {
        this.motivoDescarte = motivoDescarte;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

    // Enum para status do órgão doado
    public enum StatusOrgaoDoado {
        AGUARDANDO_IMPLANTACAO("Aguardando Implantação", "Órgão em preservação, aguardando receptor"),
        IMPLANTADO("Implantado", "Órgão foi implantado com sucesso"),
        DESCARTADO("Descartado", "Órgão foi descartado por contraindicação"),
        PROCESSANDO("Processando", "Órgão em processamento para implantação"),
        FALHA_IMPLANTACAO("Falha na Implantação", "Tentativa de implantação falhou");

        private String label;
        private String descricao;

        StatusOrgaoDoado(String label, String descricao) {
            this.label = label;
            this.descricao = descricao;
        }

        public String getLabel() {
            return label;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
