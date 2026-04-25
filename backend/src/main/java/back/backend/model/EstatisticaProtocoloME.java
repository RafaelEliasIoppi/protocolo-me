package back.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "estatistica_protocolo_me",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_estatistica_protocolo", columnNames = {"protocolo_me_id"})
       })
public class EstatisticaProtocoloME {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // RELACIONAMENTO
    // =========================

    @OneToOne(optional = false)
    @JoinColumn(name = "protocolo_me_id", nullable = false)
    @JsonIgnoreProperties({"exames", "doacao", "paciente", "centralTransplantes"})
    private ProtocoloME protocoloME;

    // =========================
    // COMPETÊNCIA
    // =========================

    private Integer anoCompetencia;
    private Integer mesCompetencia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Periodicidade periodicidade = Periodicidade.ANUAL;

    // =========================
    // DADOS DO DOADOR
    // =========================

    private String ofNac;
    private String rgctDoador;
    private String nomeDoador;
    private String hospitalNotif;
    private String municipio;
    private String faixaEtariaDoad;
    private String sexoDoad;
    private String aboDoad;

    // =========================
    // CONDIÇÕES CLÍNICAS
    // =========================

    private String resCausaMorte;
    private String dm;
    private String has;
    private String etilismo;
    private String tabagismo;

    private String crInicial;
    private String crFinal;

    // =========================
    // ÓRGÃOS
    // =========================

    private String rimD;
    private String rimE;
    private String coracao;
    private String pulmD;
    private String pulmE;
    private String figado;
    private String corneas;

    // =========================
    // DESTINAÇÃO
    // =========================

    private String destRimD;
    private String destRimE;
    private String destCoracao;
    private String destPulmD;
    private String destPulmE;
    private String destFigado;

    // =========================
    // DESCARTES
    // =========================

    private String descarteRimD;
    private String descarteRimE;
    private String descarteCoracao;
    private String descartePulmaoD;
    private String descartePulmaoE;
    private String descarteFigado;

    @Column(length = 2000)
    private String motivoDescarteEsclarecer;

    // =========================
    // RECEPTORES (resumo)
    // =========================

    private String receptorRd;
    private String receptorRe;
    private String receptorFig;
    private String receptorPulmD;
    private String receptorPulmE;
    private String receptorCor;

    // =========================
    // CONTROLE
    // =========================

    private String classif;
    private String algumOrgaoImplantadoNoRs;

    @Column(length = 4000)
    private String observacoes;

    // 🔥 FLEXIBILIDADE (layout SNT variável)
    @Lob
    private String dadosCamposJson;

    private String atualizadoPor;

    private LocalDateTime dataAtualizacao;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    // =========================
    // LIFECYCLE
    // =========================

    @PrePersist
    protected void onCreate() {
        LocalDateTime agora = LocalDateTime.now();
        dataCriacao = agora;
        dataAtualizacao = agora;
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

    // =========================
    // ENUM
    // =========================

    public enum Periodicidade {
        MENSAL,
        ANUAL
    }

    // =========================
    // GETTERS ESSENCIAIS
    // =========================

    public Long getId() { return id; }

    public ProtocoloME getProtocoloME() { return protocoloME; }
    public void setProtocoloME(ProtocoloME protocoloME) { this.protocoloME = protocoloME; }

    public Periodicidade getPeriodicidade() { return periodicidade; }
    public void setPeriodicidade(Periodicidade periodicidade) { this.periodicidade = periodicidade; }

    public String getDadosCamposJson() { return dadosCamposJson; }
    public void setDadosCamposJson(String dadosCamposJson) { this.dadosCamposJson = dadosCamposJson; }

    public String getAtualizadoPor() { return atualizadoPor; }
    public void setAtualizadoPor(String atualizadoPor) { this.atualizadoPor = atualizadoPor; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
}
