package back.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orgao_doado")
public class OrgaoDoado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "doacao_id")
    @JsonIgnoreProperties({"orgaos", "protocoloME"})
    private Doacao doacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoOrgao tipo;

    // 🔥 NOVO: lado (quando aplicável)
    @Enumerated(EnumType.STRING)
    private LadoOrgao lado;

    // 🔥 NOVO: subtipo (ex: esclera, válvula, etc)
    @Enumerated(EnumType.STRING)
    private SubtipoOrgao subtipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusOrgaoDoado status = StatusOrgaoDoado.AGUARDANDO_IMPLANTACAO;

    @Column(length = 1000)
    private String motivo;

    private String hospitalReceptor;
    private String pacienteReceptor;
    private String cpfReceptor;

    private LocalDateTime dataArmazenamento;
    private LocalDateTime dataImplantacao;
    private LocalDateTime dataDescarte;

    @Column(length = 500)
    private String motivoDescarte;

    @Column(length = 1000)
    private String observacoes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column
    private LocalDateTime dataAtualizacao;

    // =========================
    // CONSTRUTORES
    // =========================

    public OrgaoDoado() {}

    public OrgaoDoado(Doacao doacao, TipoOrgao tipo, LadoOrgao lado) {
        this.doacao = doacao;
        this.tipo = tipo;
        this.lado = lado;
    }

    // =========================
    // VALIDAÇÕES
    // =========================

    @PrePersist
    @PreUpdate
    private void validarEstado() {

        // Não pode criar órgão se doação recusada
        if (doacao != null && Boolean.FALSE.equals(doacao.getAutorizada())) {
            throw new RuntimeException("Doação não autorizada não pode ter órgãos");
        }

        // Implantado precisa de data
        if (status == StatusOrgaoDoado.IMPLANTADO && dataImplantacao == null) {
            throw new RuntimeException("Órgão implantado precisa de data de implantação");
        }

        // Descartado precisa de motivo
        if (status == StatusOrgaoDoado.DESCARTADO && motivoDescarte == null) {
            throw new RuntimeException("Órgão descartado precisa de motivo");
        }

        // Não pode implantar e descartar ao mesmo tempo
        if (dataImplantacao != null && dataDescarte != null) {
            throw new RuntimeException("Órgão não pode ser implantado e descartado ao mesmo tempo");
        }

        // 🔥 REGRA DE LADO
        if (tipo == TipoOrgao.RIM || tipo == TipoOrgao.PULMAO || tipo == TipoOrgao.CORNEA) {
            if (lado == null) {
                throw new RuntimeException("Órgão exige lado (direito/esquerdo)");
            }
        }

        // Órgãos que NÃO devem ter lado
        if (tipo == TipoOrgao.FIGADO || tipo == TipoOrgao.CORACAO || tipo == TipoOrgao.PANCREAS) {
            if (lado != null) {
                throw new RuntimeException("Este órgão não possui lado");
            }
        }
    }

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
    // ENUMS
    // =========================

    public enum TipoOrgao {
        RIM,
        FIGADO,
        CORACAO,
        PULMAO,
        PANCREAS,
        CORNEA,
        PELE,
        OSSO,
        VALVA_CARDIACA
    }

    public enum LadoOrgao {
        DIREITO,
        ESQUERDO
    }

    public enum SubtipoOrgao {
        // Córnea
        CORNEA_TOTAL,
        ESCLERA,

        // Valva
        VALVA_AORTICA,
        VALVA_MITRAL,

        // Outros
        ENXERTO_OSSEO,
        PELE_TOTAL
    }

    public enum StatusOrgaoDoado {
        AGUARDANDO_IMPLANTACAO,
        EM_CAPTACAO,
        EM_TRANSPORTE,
        IMPLANTADO,
        DESCARTADO,
        FALHA_IMPLANTACAO
    }

    // =========================
    // GETTERS E SETTERS
    // =========================

    public Long getId() { return id; }

    public Doacao getDoacao() { return doacao; }
    public void setDoacao(Doacao doacao) { this.doacao = doacao; }

    public TipoOrgao getTipo() { return tipo; }
    public void setTipo(TipoOrgao tipo) { this.tipo = tipo; }

    public LadoOrgao getLado() { return lado; }
    public void setLado(LadoOrgao lado) { this.lado = lado; }

    public SubtipoOrgao getSubtipo() { return subtipo; }
    public void setSubtipo(SubtipoOrgao subtipo) { this.subtipo = subtipo; }

    public StatusOrgaoDoado getStatus() { return status; }
    public void setStatus(StatusOrgaoDoado status) { this.status = status; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getHospitalReceptor() { return hospitalReceptor; }
    public void setHospitalReceptor(String hospitalReceptor) { this.hospitalReceptor = hospitalReceptor; }

    public String getPacienteReceptor() { return pacienteReceptor; }
    public void setPacienteReceptor(String pacienteReceptor) { this.pacienteReceptor = pacienteReceptor; }

    public String getCpfReceptor() { return cpfReceptor; }
    public void setCpfReceptor(String cpfReceptor) { this.cpfReceptor = cpfReceptor; }

    public LocalDateTime getDataImplantacao() { return dataImplantacao; }
    public void setDataImplantacao(LocalDateTime dataImplantacao) { this.dataImplantacao = dataImplantacao; }

    public LocalDateTime getDataDescarte() { return dataDescarte; }
    public void setDataDescarte(LocalDateTime dataDescarte) { this.dataDescarte = dataDescarte; }

}
