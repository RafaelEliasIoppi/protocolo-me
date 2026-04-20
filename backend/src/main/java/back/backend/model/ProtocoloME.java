package back.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "protocolo_me")
public class ProtocoloME {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "central_transplantes_id")
    @JsonIgnoreProperties({"protocolosME", "usuarios", "hospitaisParceados", "hibernateLazyInitializer", "handler"})
    private CentralTransplantes centralTransplantes;

    @ManyToOne(optional = false)
    @JoinColumn(name = "paciente_id")
    @JsonIgnoreProperties({"protocolosME", "examesEmProtocolo", "hibernateLazyInitializer", "handler"})
    private Paciente paciente;

    @Column(nullable = false)
    private String numeroProtocolo;

    @Column(nullable = false)
    private String hospitalOrigem;

    @Column
    private String medicoResponsavel;

    @Column
    private String enfermeiro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusProtocoloME status = StatusProtocoloME.NOTIFICADO;

    @Column
    private String diagnosticoBasico;

    @Column
    private String causaMorte;

    @Column(length = 2000)
    private String observacoes;

    @Column
    private Boolean testeClinico1Realizado = false;

    @Column
    private LocalDateTime dataTesteClinico1;

    @Column
    private Boolean testeClinico2Realizado = false;

    @Column
    private LocalDateTime dataTesteClinico2;

    @Column
    private Boolean testesComplementaresRealizados = false;

    @Column
    private String testesComplementares;

    @Column
    private LocalDateTime dataTesteComplementar;

    @Column
    private Boolean familiaNotificada = false;

    @Column
    private LocalDateTime dataNotificacaoFamilia;

    @Column
    private Boolean autopsiaAutorizada = false;

    @Column
    private String orgaosDisponiveis;

    @Column
    private Boolean preservacaoOrgaos = false;

    @Column
    private LocalDateTime dataPreservacao;

    @Column(name = "data_notificacao", nullable = false)
    private LocalDateTime dataNotificacao;

    @Column
    private LocalDateTime dataConfirmacaoME;

    @Column
    private LocalDateTime dataSaidaHospital;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @OneToMany(mappedBy = "protocoloME", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"protocoloME"})
    private List<ExameME> exames;

    @OneToMany(mappedBy = "protocoloME", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"protocoloME"})
    private List<OrgaoDoado> orgaosDoados;

    // Construtores
    public ProtocoloME() {}

    public ProtocoloME(Long id, CentralTransplantes centralTransplantes, Paciente paciente, String numeroProtocolo, 
                      String hospitalOrigem, String medicoResponsavel, String enfermeiro, StatusProtocoloME status, 
                      String diagnosticoBasico, String causaMorte, String observacoes, Boolean testeClinico1Realizado, 
                      LocalDateTime dataTesteClinico1, Boolean testeClinico2Realizado, LocalDateTime dataTesteClinico2, 
                      Boolean testesComplementaresRealizados, String testesComplementares, LocalDateTime dataTesteComplementar, 
                      Boolean familiaNotificada, LocalDateTime dataNotificacaoFamilia, Boolean autopsiaAutorizada, 
                      String orgaosDisponiveis, Boolean preservacaoOrgaos, LocalDateTime dataPreservacao, 
                      LocalDateTime dataNotificacao, LocalDateTime dataConfirmacaoME, LocalDateTime dataSaidaHospital, 
                      LocalDateTime dataCriacao, LocalDateTime dataAtualizacao, List<ExameME> exames) {
        this.id = id;
        this.centralTransplantes = centralTransplantes;
        this.paciente = paciente;
        this.numeroProtocolo = numeroProtocolo;
        this.hospitalOrigem = hospitalOrigem;
        this.medicoResponsavel = medicoResponsavel;
        this.enfermeiro = enfermeiro;
        this.status = status;
        this.diagnosticoBasico = diagnosticoBasico;
        this.causaMorte = causaMorte;
        this.observacoes = observacoes;
        this.testeClinico1Realizado = testeClinico1Realizado;
        this.dataTesteClinico1 = dataTesteClinico1;
        this.testeClinico2Realizado = testeClinico2Realizado;
        this.dataTesteClinico2 = dataTesteClinico2;
        this.testesComplementaresRealizados = testesComplementaresRealizados;
        this.testesComplementares = testesComplementares;
        this.dataTesteComplementar = dataTesteComplementar;
        this.familiaNotificada = familiaNotificada;
        this.dataNotificacaoFamilia = dataNotificacaoFamilia;
        this.autopsiaAutorizada = autopsiaAutorizada;
        this.orgaosDisponiveis = orgaosDisponiveis;
        this.preservacaoOrgaos = preservacaoOrgaos;
        this.dataPreservacao = dataPreservacao;
        this.dataNotificacao = dataNotificacao;
        this.dataConfirmacaoME = dataConfirmacaoME;
        this.dataSaidaHospital = dataSaidaHospital;
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
        this.exames = exames;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CentralTransplantes getCentralTransplantes() {
        return centralTransplantes;
    }

    public void setCentralTransplantes(CentralTransplantes centralTransplantes) {
        this.centralTransplantes = centralTransplantes;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public String getHospitalOrigem() {
        return hospitalOrigem;
    }

    public void setHospitalOrigem(String hospitalOrigem) {
        this.hospitalOrigem = hospitalOrigem;
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

    public StatusProtocoloME getStatus() {
        return status;
    }

    public void setStatus(StatusProtocoloME status) {
        this.status = status;
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

    public Boolean getTesteClinico1Realizado() {
        return testeClinico1Realizado;
    }

    public void setTesteClinico1Realizado(Boolean testeClinico1Realizado) {
        this.testeClinico1Realizado = testeClinico1Realizado;
    }

    public LocalDateTime getDataTesteClinico1() {
        return dataTesteClinico1;
    }

    public void setDataTesteClinico1(LocalDateTime dataTesteClinico1) {
        this.dataTesteClinico1 = dataTesteClinico1;
    }

    public Boolean getTesteClinico2Realizado() {
        return testeClinico2Realizado;
    }

    public void setTesteClinico2Realizado(Boolean testeClinico2Realizado) {
        this.testeClinico2Realizado = testeClinico2Realizado;
    }

    public LocalDateTime getDataTesteClinico2() {
        return dataTesteClinico2;
    }

    public void setDataTesteClinico2(LocalDateTime dataTesteClinico2) {
        this.dataTesteClinico2 = dataTesteClinico2;
    }

    public Boolean getTestesComplementaresRealizados() {
        return testesComplementaresRealizados;
    }

    public void setTestesComplementaresRealizados(Boolean testesComplementaresRealizados) {
        this.testesComplementaresRealizados = testesComplementaresRealizados;
    }

    public String getTestesComplementares() {
        return testesComplementares;
    }

    public void setTestesComplementares(String testesComplementares) {
        this.testesComplementares = testesComplementares;
    }

    public LocalDateTime getDataTesteComplementar() {
        return dataTesteComplementar;
    }

    public void setDataTesteComplementar(LocalDateTime dataTesteComplementar) {
        this.dataTesteComplementar = dataTesteComplementar;
    }

    public Boolean getFamiliaNotificada() {
        return familiaNotificada;
    }

    public void setFamiliaNotificada(Boolean familiaNotificada) {
        this.familiaNotificada = familiaNotificada;
    }

    public LocalDateTime getDataNotificacaoFamilia() {
        return dataNotificacaoFamilia;
    }

    public void setDataNotificacaoFamilia(LocalDateTime dataNotificacaoFamilia) {
        this.dataNotificacaoFamilia = dataNotificacaoFamilia;
    }

    public Boolean getAutopsiaAutorizada() {
        return autopsiaAutorizada;
    }

    public void setAutopsiaAutorizada(Boolean autopsiaAutorizada) {
        this.autopsiaAutorizada = autopsiaAutorizada;
    }

    public String getOrgaosDisponiveis() {
        return orgaosDisponiveis;
    }

    public void setOrgaosDisponiveis(String orgaosDisponiveis) {
        this.orgaosDisponiveis = orgaosDisponiveis;
    }

    public Boolean getPreservacaoOrgaos() {
        return preservacaoOrgaos;
    }

    public void setPreservacaoOrgaos(Boolean preservacaoOrgaos) {
        this.preservacaoOrgaos = preservacaoOrgaos;
    }

    public LocalDateTime getDataPreservacao() {
        return dataPreservacao;
    }

    public void setDataPreservacao(LocalDateTime dataPreservacao) {
        this.dataPreservacao = dataPreservacao;
    }

    public LocalDateTime getDataNotificacao() {
        return dataNotificacao;
    }

    public void setDataNotificacao(LocalDateTime dataNotificacao) {
        this.dataNotificacao = dataNotificacao;
    }

    public LocalDateTime getDataConfirmacaoME() {
        return dataConfirmacaoME;
    }

    public void setDataConfirmacaoME(LocalDateTime dataConfirmacaoME) {
        this.dataConfirmacaoME = dataConfirmacaoME;
    }

    public LocalDateTime getDataSaidaHospital() {
        return dataSaidaHospital;
    }

    public void setDataSaidaHospital(LocalDateTime dataSaidaHospital) {
        this.dataSaidaHospital = dataSaidaHospital;
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

    public List<ExameME> getExames() {
        return exames;
    }

    public void setExames(List<ExameME> exames) {
        this.exames = exames;
    }

    public List<OrgaoDoado> getOrgaosDoados() {
        return orgaosDoados;
    }

    public void setOrgaosDoados(List<OrgaoDoado> orgaosDoados) {
        this.orgaosDoados = orgaosDoados;
    }

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
        if (dataNotificacao == null) {
            dataNotificacao = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

    /**
     * Calcula o status automático do protocolo baseado nos exames realizados
     * e na etapa de entrevista familiar
     */
    public StatusProtocoloME calcularStatusAutomatico() {
        // Se família recusou, status é FAMILIA_RECUSOU
        if (familiaNotificada && !autopsiaAutorizada && this.status == StatusProtocoloME.FAMILIA_RECUSOU) {
            return StatusProtocoloME.FAMILIA_RECUSOU;
        }

        // Se já passou pela entrevista familiar e foi autorizado
        if (familiaNotificada && autopsiaAutorizada) {
            return StatusProtocoloME.DOACAO_AUTORIZADA;
        }

        // Se morte cerebral foi confirmada (testes + complementares)
        if (testeClinico1Realizado && testeClinico2Realizado && testesComplementaresRealizados) {
            return StatusProtocoloME.MORTE_CEREBRAL_CONFIRMADA;
        }

        // Se passou pelo menos um teste clínico
        if (testeClinico1Realizado || testeClinico2Realizado) {
            return StatusProtocoloME.EM_PROCESSO;
        }

        // Status inicial
        return StatusProtocoloME.NOTIFICADO;
    }

    /**
     * Verifica se todos os testes necessários foram realizados (pronto para entrevista)
     */
    public boolean estaProtoProntoParaEntrevista() {
        return Boolean.TRUE.equals(testeClinico1Realizado) &&
               Boolean.TRUE.equals(testeClinico2Realizado) &&
               Boolean.TRUE.equals(testesComplementaresRealizados) &&
               Boolean.TRUE.equals(dataConfirmacaoME != null);
    }

    public enum StatusProtocoloME {
        NOTIFICADO("Notificado", "Central notificada - Aguardando testes clínicos"),
        EM_PROCESSO("Em Processo", "Testes clínicos em andamento"),
        MORTE_CEREBRAL_CONFIRMADA("Morte Cerebral Confirmada", "Morte cerebral confirmada por exames"),
        ENTREVISTA_FAMILIAR("Entrevista Familiar", "Aguardando entrevista com a família"),
        FAMILIA_RECUSOU("Família Recusou", "Família recusou a doação"),
        DOACAO_AUTORIZADA("Doação Autorizada", "Autorização familiar obtida"),
        CONTRAINDICADO("Contraindicado", "Contraindicação para doação"),
        FINALIZADO("Finalizado", "Protocolo finalizado");

        private String label;
        private String descricao;

        StatusProtocoloME(String label, String descricao) {
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
