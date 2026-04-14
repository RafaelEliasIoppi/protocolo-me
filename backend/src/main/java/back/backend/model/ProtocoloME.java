package back.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "protocolo_me")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProtocoloME {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "central_transplantes_id")
    private CentralTransplantes centralTransplantes;

    @ManyToOne(optional = false)
    @JoinColumn(name = "paciente_id")
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
    private List<ExameME> exames;

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

    public enum StatusProtocoloME {
        NOTIFICADO("Notificado", "Central notificada"),
        EM_PROCESSO("Em Processo", "Testes clínicos em andamento"),
        MORTE_CEREBRAL_CONFIRMADA("Morte Cerebral Confirmada", "ME confirmada"),
        FAMILIA_INFORMADA("Família Informada", "Família foi informada"),
        ORGAOS_PRESERVADOS("Órgãos Preservados", "Órgãos em preservação"),
        APTO_TRANSPLANTE("Apto para Transplante", "Doador apto para transplante"),
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
