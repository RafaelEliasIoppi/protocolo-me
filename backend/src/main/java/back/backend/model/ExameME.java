package back.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "exame_me")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExameME {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "protocolo_me_id")
    private ProtocoloME protocoloME;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaExame categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoExame tipoExame;

    @Column(nullable = false)
    private String descricao;

    @Column
    private String resultado;

    @Column
    private Boolean resultado_positivo;

    @Column
    private LocalDateTime dataRealizacao;

    @Column
    private String responsavel;

    @Column(length = 1000)
    private String observacoes;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

    public enum CategoriaExame {
        CLINICO("Exame Clínico", "Testes neurológicos clínicos"),
        COMPLEMENTAR("Exame Complementar", "Testes complementares de imagem/eletroencefalograma"),
        LABORATORIAL("Exame Laboratorial", "Análises laboratoriais");

        private String label;
        private String descricao;

        CategoriaExame(String label, String descricao) {
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

    public enum TipoExame {
        // Exames Clínicos
        RESPOSTA_ESTIMULO_DORO("Resposta ao Estímulo Doloroso", CategoriaExame.CLINICO),
        REFLEXO_PUPILAR("Reflexo Pupilar", CategoriaExame.CLINICO),
        REFLEXO_CORNEAL("Reflexo Corneal", CategoriaExame.CLINICO),
        REFLEXO_VESTIBULO_OCULAR("Reflexo Vestibulo-Ocular (Calórico)", CategoriaExame.CLINICO),
        REFLEXO_NAUSEOSO("Reflexo Nauseoso/Faríngeo", CategoriaExame.CLINICO),
        REFLEXO_TOSSE("Reflexo de Tosse", CategoriaExame.CLINICO),
        APNEIA_TEST("Teste de Apneia", CategoriaExame.CLINICO),
        POSTURA_DECEREBRADO("Postura Decerebrada", CategoriaExame.CLINICO),
        POSTURA_DESCEREBRADO("Postura Descerebrado", CategoriaExame.CLINICO),

        // Exames Complementares - Imagem
        ANGIOGRAFIA_CEREBRAL("Angiografia Cerebral Digital", CategoriaExame.COMPLEMENTAR),
        RESSONANCIA_MAGNETICA("Ressonância Magnética", CategoriaExame.COMPLEMENTAR),
        TOMOGRAFIA_CRANIO("Tomografia de Crânio", CategoriaExame.COMPLEMENTAR),
        TOMOGRAFIA_ANGIO("Tomografia Angio", CategoriaExame.COMPLEMENTAR),
        ULTRASSOM_DOPPLER("Ultrassom Doppler Transcraniano", CategoriaExame.COMPLEMENTAR),

        // Exames Complementares - Eletrofisiologia
        ELETROENCEFALOGRAMA("Eletroencefalograma (EEG)", CategoriaExame.COMPLEMENTAR),
        MAPEAMENTO_CEREBRAL("Mapeamento Cerebral", CategoriaExame.COMPLEMENTAR),
        RESSONANCIA_MAGNETICA_FUNCIONAL("Ressonância Magnética Funcional", CategoriaExame.COMPLEMENTAR),

        // Exames Laboratoriais - Gasometria e Sangue
        GASOMETRIA_ARTERIAL("Gasometria Arterial", CategoriaExame.LABORATORIAL),
        HEMOGRAMA("Hemograma Completo", CategoriaExame.LABORATORIAL),
        ELETRÓLITOS("Eletrólitos (Na, K, Cl)", CategoriaExame.LABORATORIAL),
        GLICEMIA("Glicemia", CategoriaExame.LABORATORIAL),
        CALCIO("Cálcio Iônico", CategoriaExame.LABORATORIAL),

        // Exames Laboratoriais - Função de Órgãos
        FUNCAO_HEPATICA("Função Hepática (AST, ALT, Bilirrubina)", CategoriaExame.LABORATORIAL),
        FUNCAO_RENAL("Função Renal (Creatinina, Uréia)", CategoriaExame.LABORATORIAL),
        COAGULACAO("Testes de Coagulação (PT, APPT)", CategoriaExame.LABORATORIAL),
        PROTEINAS_TOTAIS("Proteínas Totais", CategoriaExame.LABORATORIAL),

        // Exames Laboratoriais - Infecciosos
        SOROLOGIA_HIV("Sorologia HIV", CategoriaExame.LABORATORIAL),
        SOROLOGIA_HEPATITE_B("Sorologia Hepatite B", CategoriaExame.LABORATORIAL),
        SOROLOGIA_HEPATITE_C("Sorologia Hepatite C", CategoriaExame.LABORATORIAL),
        SOROLOGIA_SIFILIS("Sorologia Sífilis (RPR/VDRL)", CategoriaExame.LABORATORIAL),
        CULTURA_SANGUE("Hemocultura", CategoriaExame.LABORATORIAL),

        // Exames Laboratoriais - Outros
        TIPAGEM_SANGUINEA("Tipagem Sanguínea", CategoriaExame.LABORATORIAL),
        SOROLOGIAS_DIVERSAS("Sorologias Diversas", CategoriaExame.LABORATORIAL),
        TESTE_FUNCAO_TIREOIDE("Teste de Função Tireoidiana", CategoriaExame.LABORATORIAL),
        LACTATO("Lactato Sérico", CategoriaExame.LABORATORIAL);

        private String label;
        private CategoriaExame categoria;

        TipoExame(String label, CategoriaExame categoria) {
            this.label = label;
            this.categoria = categoria;
        }

        public String getLabel() {
            return label;
        }

        public CategoriaExame getCategoria() {
            return categoria;
        }
    }
    // Getters e Setters manuais
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

    public CategoriaExame getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaExame categoria) {
        this.categoria = categoria;
    }

    public TipoExame getTipoExame() {
        return tipoExame;
    }

    public void setTipoExame(TipoExame tipoExame) {
        this.tipoExame = tipoExame;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public Boolean getResultado_positivo() {
        return resultado_positivo;
    }

    public void setResultado_positivo(Boolean resultado_positivo) {
        this.resultado_positivo = resultado_positivo;
    }

    public LocalDateTime getDataRealizacao() {
        return dataRealizacao;
    }

    public void setDataRealizacao(LocalDateTime dataRealizacao) {
        this.dataRealizacao = dataRealizacao;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
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
    }}
