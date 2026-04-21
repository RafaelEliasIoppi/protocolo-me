package back.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "estatistica_protocolo_me", uniqueConstraints = {
        @UniqueConstraint(name = "uk_estatistica_protocolo", columnNames = {"protocolo_me_id"})
})
public class EstatisticaProtocoloME {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "protocolo_me_id", nullable = false)
    @JsonIgnoreProperties({"exames", "orgaosDoados", "paciente", "centralTransplantes", "hibernateLazyInitializer", "handler"})
    private ProtocoloME protocoloME;

    @Column(name = "ano_competencia")
    private Integer anoCompetencia;

    @Column(name = "mes_competencia")
    private Integer mesCompetencia;

    @Column(name = "periodicidade", nullable = false)
    private String periodicidade = "ANUAL";

    @Column private String ofNac;
    @Column private String rgctDoador;
    @Column private String nomeDoador;
    @Column private String hospitalNotif;
    @Column private String dataOf;
    @Column private String regPdot;
    @Column private String regOf;
    @Column private String mes;
    @Column private String municipio;
    @Column private String idDoad;
    @Column private String faixaEtariaDoad;
    @Column private String sexoDoad;
    @Column private String aboDoad;
    @Column private String resCausaMorte;
    @Column private String dm;
    @Column private String has;
    @Column private String etilismo;
    @Column private String tabagismo;
    @Column private String crInicial;
    @Column private String crFinal;
    @Column private String rimD;
    @Column private String rimE;
    @Column private String coracao;
    @Column private String pulmD;
    @Column private String pulmE;
    @Column private String figado;
    @Column private String corneas;
    @Column private String pele;
    @Column private String ossoMusculo;
    @Column private String destRimD;
    @Column private String destRimE;
    @Column private String destCoracao;
    @Column private String destPulmD;
    @Column private String destPulmE;
    @Column private String destFigado;
    @Column private String txRinsBloco;
    @Column private String txPulmBilat;
    @Column private String txRimFig;
    @Column private String txPulmDRim;
    @Column private String txPulmERim;
    @Column private String txCorRim;
    @Column private String txCorPulm;
    @Column private String descarteRimD;
    @Column private String descarteRimE;
    @Column private String descarteCoracao;
    @Column private String descartePulmaoD;
    @Column private String descartePulmaoE;
    @Column private String descarteFigado;
    @Column private String motivoDescarteEsclarecer;
    @Column private String hospEquipeRecRd;
    @Column private String rgctRd;
    @Column private String receptorRd;
    @Column private String idadeRecRd;
    @Column private String sexoRecRd;
    @Column private String mesTxRd;
    @Column private String hospEquipeRecRe;
    @Column private String rgctRe;
    @Column private String receptorRe;
    @Column private String idadeRecRe;
    @Column private String sexoRecRe;
    @Column private String mesTxRe;
    @Column private String hospEquipeRecFig;
    @Column private String rgctFig;
    @Column private String receptorFig;
    @Column private String idadeRecFig;
    @Column private String sexoRecFig;
    @Column private String mesTxFig;
    @Column private String hospEquipeRecPulmD;
    @Column private String rgctPulmD;
    @Column private String receptorPulmD;
    @Column private String idadeRecPulmD;
    @Column private String sexoRecPulmD;
    @Column private String mesTxPulmD;
    @Column private String hospEquipeRecPulmE;
    @Column private String rgctPulmE;
    @Column private String receptorPulmE;
    @Column private String idadeRecPulmE;
    @Column private String sexoRecPulmE;
    @Column private String mesTxPulmE;
    @Column private String hospEquipeRecCor;
    @Column private String rgctCor;
    @Column private String receptorCor;
    @Column private String idadeRecCor;
    @Column private String sexoRecCor;
    @Column private String mesTxCor;
    @Column private String doadorOfertaNacional;
    @Column private String classif;
    @Column private String algumOrgaoImplantadoNoRs;
    @Column private String recusaRim;
    @Column private String recusaFigado;
    @Column private String recusaCoracao;
    @Column private String recusaPulmao;
    @Column(length = 4000)
    private String observacoes;

    @Lob
    @Column(name = "dados_campos_json")
    private String dadosCamposJson;

    @Column(name = "atualizado_por")
    private String atualizadoPor;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

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

    public Integer getAnoCompetencia() {
        return anoCompetencia;
    }

    public void setAnoCompetencia(Integer anoCompetencia) {
        this.anoCompetencia = anoCompetencia;
    }

    public Integer getMesCompetencia() {
        return mesCompetencia;
    }

    public void setMesCompetencia(Integer mesCompetencia) {
        this.mesCompetencia = mesCompetencia;
    }

    public String getPeriodicidade() {
        return periodicidade;
    }

    public void setPeriodicidade(String periodicidade) {
        this.periodicidade = periodicidade;
    }

    public String getDadosCamposJson() {
        return dadosCamposJson;
    }

    public void setDadosCamposJson(String dadosCamposJson) {
        this.dadosCamposJson = dadosCamposJson;
    }

    public String getAtualizadoPor() {
        return atualizadoPor;
    }

    public void setAtualizadoPor(String atualizadoPor) {
        this.atualizadoPor = atualizadoPor;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

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
}
