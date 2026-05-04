package back.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "protocolo_me")
@Getter
@Setter
public class ProtocoloME {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // RELACIONAMENTOS
    // =========================

    @ManyToOne(optional = false)
    @JoinColumn(name = "central_transplantes_id")
    @JsonIgnoreProperties({ "protocolosME", "usuarios", "hospitaisParceados" })
    private CentralTransplantes centralTransplantes;

    @ManyToOne(optional = false)
    @JoinColumn(name = "paciente_id")
    @JsonIgnoreProperties({ "protocolosME", "examesEmProtocolo" })
    private Paciente paciente;

    @OneToOne(mappedBy = "protocoloME", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({ "protocoloME" })
    private Doacao doacao;

    @OneToMany(mappedBy = "protocoloME", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({ "protocoloME" })
    private List<ExameME> exames;

    // =========================
    // DADOS GERAIS
    // =========================

    @Column(nullable = false)
    private String numeroProtocolo;

    @Column(nullable = false)
    private String hospitalOrigem;

    private String medicoResponsavel;
    private String enfermeiro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusProtocoloME status = StatusProtocoloME.NOTIFICADO;

    private String diagnosticoBasico;
    private String causaMorte;

    @Column(length = 2000)
    private String observacoes;

    // =========================
    // TESTES DE MORTE ENCEFÁLICA
    // =========================

    private Boolean testeClinico1Realizado = false;
    private LocalDateTime dataTesteClinico1;

    private Boolean testeClinico2Realizado = false;
    private LocalDateTime dataTesteClinico2;

    private Boolean testesComplementaresRealizados = false;
    private String testesComplementares;
    private LocalDateTime dataTesteComplementar;

    private LocalDateTime dataConfirmacaoME;

    // =========================
    // VALIDAÇÃO DE TESTES PELA CENTRAL
    // =========================

    private Boolean testeClinico1Validado = false;
    private LocalDateTime dataValidacaoTesteClinico1;

    private Boolean testeClinico2Validado = false;
    private LocalDateTime dataValidacaoTesteClinico2;

    private Boolean testesComplementaresValidados = false;
    private LocalDateTime dataValidacaoTesteComplementar;

    private Boolean apneiaValidada = false;
    private LocalDateTime dataValidacaoApneia;

    private String validadosPor;
    private LocalDateTime dataValidacaoGeral;

    // =========================
    // ENTREVISTA FAMILIAR
    // =========================

    private Boolean familiaNotificada = false;
    private LocalDateTime dataNotificacaoFamilia;

    // =========================
    // AUTOPSIA E PRESERVAÇÃO
    // =========================

    private Boolean autopsiaAutorizada = false;
    private Boolean preservacaoOrgaos = false;
    private LocalDateTime dataPreservacao;
    private String orgaosDisponiveis;

    // =========================
    // CONTROLE
    // =========================

    @Column(nullable = false)
    private LocalDateTime dataNotificacao;

    private LocalDateTime dataSaidaHospital;

    @Column(length = 4000)
    private String relatorioFinalEditavel;

    private String relatorioFinalAtualizadoPor;
    private LocalDateTime relatorioFinalAtualizadoEm;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    private LocalDateTime dataAtualizacao;

    // =========================
    // LIFECYCLE
    // =========================

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

    // =========================
    // REGRA DE NEGÓCIO (MELHORADA)
    // =========================

    public StatusProtocoloME calcularStatusAutomatico() {

        // ✅ NOVA REGRA: Verificar se exames estão VALIDADOS (não apenas realizados)
        boolean testesValidados = Boolean.TRUE.equals(testeClinico1Validado) &&
                Boolean.TRUE.equals(testeClinico2Validado) &&
                Boolean.TRUE.equals(testesComplementaresValidados) &&
                Boolean.TRUE.equals(apneiaValidada);

        boolean testesRealizados = Boolean.TRUE.equals(testeClinico1Realizado) &&
                Boolean.TRUE.equals(testeClinico2Realizado) &&
                Boolean.TRUE.equals(testesComplementaresRealizados);

        // 🔹 Ainda não tem exames realizados
        if (!testesRealizados) {
            if (Boolean.TRUE.equals(testeClinico1Realizado) ||
                    Boolean.TRUE.equals(testeClinico2Realizado)) {
                return StatusProtocoloME.EM_PROCESSO;
            }
            return StatusProtocoloME.NOTIFICADO;
        }

        // 🔹 Exames realizados mas aguardando validação da central
        if (!testesValidados) {
            return StatusProtocoloME.EM_PROCESSO;
        }

        // 🔹 Confirmou morte encefálica
        if (dataConfirmacaoME == null) {
            return StatusProtocoloME.MORTE_CEREBRAL_CONFIRMADA;
        }

        // 🔹 Precisa entrevistar família
        if (doacao == null) {
            return StatusProtocoloME.ENTREVISTA_FAMILIAR;
        }

        // 🔹 Já tem decisão
        if (doacao.getAutorizada() != null) {

            if (!doacao.getAutorizada()) {
                return StatusProtocoloME.FAMILIA_RECUSOU;
            }

            return StatusProtocoloME.DOACAO_AUTORIZADA;
        }

        return StatusProtocoloME.ENTREVISTA_FAMILIAR;
    }

    public boolean estaProntoParaEntrevista() {
        // ✅ NOVA REGRA: Exames devem estar VALIDADOS pela central
        return Boolean.TRUE.equals(testeClinico1Validado) &&
                Boolean.TRUE.equals(testeClinico2Validado) &&
                Boolean.TRUE.equals(testesComplementaresValidados) &&
                Boolean.TRUE.equals(apneiaValidada) &&
                dataConfirmacaoME != null;
    }

    // =========================
    // ENUM
    // =========================

    public enum StatusProtocoloME {
        NOTIFICADO("Notificado"),
        EM_PROCESSO("Em Processo"),
        MORTE_CEREBRAL_CONFIRMADA("Morte Cerebral Confirmada"),
        ENTREVISTA_FAMILIAR("Entrevista Familiar"),
        FAMILIA_RECUSOU("Família Recusou"),
        DOACAO_AUTORIZADA("Doação Autorizada"),
        CONTRAINDICADO("Contraindicado"),
        FINALIZADO("Finalizado");

        private final String label;

        StatusProtocoloME(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    // =========================
    // GETTERS ESSENCIAIS
    // =========================

    public Long getId() {
        return id;
    }

    public CentralTransplantes getCentralTransplantes() {
        return centralTransplantes;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public Doacao getDoacao() {
        return doacao;
    }

    public StatusProtocoloME getStatus() {
        return status;
    }

    public void setStatus(StatusProtocoloME status) {
        this.status = status;
    }

    public LocalDateTime getDataConfirmacaoME() {
        return dataConfirmacaoME;
    }

    public Boolean getAutopsiaAutorizada() {
        return autopsiaAutorizada;
    }

    public void setAutopsiaAutorizada(Boolean autopsiaAutorizada) {
        this.autopsiaAutorizada = autopsiaAutorizada;
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

    public String getOrgaosDisponiveis() {
        return orgaosDisponiveis;
    }

    public void setOrgaosDisponiveis(String orgaosDisponiveis) {
        this.orgaosDisponiveis = orgaosDisponiveis;
    }
}
