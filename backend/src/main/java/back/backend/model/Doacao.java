package back.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "doacao")
public class Doacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔥 vínculo com protocolo (1:1)
    @OneToOne(optional = false)
    @JoinColumn(name = "protocolo_me_id", unique = true)
    @JsonIgnoreProperties({ "doacao", "exames", "paciente", "centralTransplantes" })
    private ProtocoloME protocoloME;

    // 🔥 NOVO: vínculo com central
    @ManyToOne(optional = false)
    @JoinColumn(name = "central_id")
    @JsonIgnoreProperties({ "protocolosME", "doacoes" })
    private CentralTransplantes centralTransplantes;

    // 👨‍👩‍👧 Dados da entrevista familiar
    private String responsavelFamiliar;
    private String parentesco;

    private LocalDateTime dataEntrevista;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    // 📊 Status único (REMOVE boolean!)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusDoacao status = StatusDoacao.EM_ANALISE;

    // 🫀 Órgãos
    @OneToMany(mappedBy = "doacao", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({ "doacao" })
    private Set<OrgaoDoado> orgaos = new LinkedHashSet<>();

    // ⏱️ Timeline
    private LocalDateTime dataAutorizacao;
    private LocalDateTime dataFinalizacao;
    private LocalDateTime dataCancelamento;

    private String motivoCancelamento;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column
    private LocalDateTime dataAtualizacao;

    // =========================
    // LIFECYCLE
    // =========================

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

    // =========================
    // ENUM
    // =========================

    public enum StatusDoacao {
        EM_ANALISE,
        AUTORIZADA,
        RECUSADA,
        EM_CAPTACAO,
        FINALIZADA,
        CANCELADA
    }

    // =========================
    // REGRAS DE NEGÓCIO
    // =========================

    public boolean isAutorizada() {
        return StatusDoacao.AUTORIZADA.equals(this.status) ||
                StatusDoacao.EM_CAPTACAO.equals(this.status) ||
                StatusDoacao.FINALIZADA.equals(this.status);
    }

    public Boolean getAutorizada() {
        return isAutorizada();
    }

    public void setAutorizada(Boolean autorizada) {
        if (autorizada == null) {
            this.status = StatusDoacao.EM_ANALISE;
            return;
        }
        this.status = autorizada ? StatusDoacao.AUTORIZADA : StatusDoacao.RECUSADA;
    }

    // =========================
    // GETTERS E SETTERS
    // =========================

    public Long getId() {
        return id;
    }

    public ProtocoloME getProtocoloME() {
        return protocoloME;
    }

    public void setProtocoloME(ProtocoloME protocoloME) {
        this.protocoloME = protocoloME;
    }

    public CentralTransplantes getCentralTransplantes() {
        return centralTransplantes;
    }

    public void setCentralTransplantes(CentralTransplantes centralTransplantes) {
        this.centralTransplantes = centralTransplantes;
    }

    public String getResponsavelFamiliar() {
        return responsavelFamiliar;
    }

    public void setResponsavelFamiliar(String responsavelFamiliar) {
        this.responsavelFamiliar = responsavelFamiliar;
    }

    public String getParentesco() {
        return parentesco;
    }

    public void setParentesco(String parentesco) {
        this.parentesco = parentesco;
    }

    public LocalDateTime getDataEntrevista() {
        return dataEntrevista;
    }

    public void setDataEntrevista(LocalDateTime dataEntrevista) {
        this.dataEntrevista = dataEntrevista;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public StatusDoacao getStatus() {
        return status;
    }

    public void setStatus(StatusDoacao status) {
        this.status = status;
    }

    public Set<OrgaoDoado> getOrgaos() {
        return orgaos;
    }

    public void setOrgaos(Set<OrgaoDoado> orgaos) {
        this.orgaos = orgaos;
    }

    public LocalDateTime getDataAutorizacao() {
        return dataAutorizacao;
    }

    public void setDataAutorizacao(LocalDateTime dataAutorizacao) {
        this.dataAutorizacao = dataAutorizacao;
    }

    public LocalDateTime getDataFinalizacao() {
        return dataFinalizacao;
    }

    public void setDataFinalizacao(LocalDateTime dataFinalizacao) {
        this.dataFinalizacao = dataFinalizacao;
    }

    public LocalDateTime getDataCancelamento() {
        return dataCancelamento;
    }

    public void setDataCancelamento(LocalDateTime dataCancelamento) {
        this.dataCancelamento = dataCancelamento;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }
}
