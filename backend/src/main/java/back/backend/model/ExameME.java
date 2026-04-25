package back.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "exame_me")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "protocoloME")
@EqualsAndHashCode(of = "id")
public class ExameME {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "protocolo_me_id")
    @JsonIgnoreProperties({"exames", "paciente", "centralTransplantes"})
    private ProtocoloME protocoloME;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoExame tipoExame;

    @Enumerated(EnumType.STRING)
    private ResultadoExame resultado;

    private LocalDateTime dataRealizacao;
    private String responsavel;

    @Column(length = 1000)
    private String observacoes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

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

    @Transient
    public CategoriaExame getCategoria() {
        return tipoExame != null ? tipoExame.getCategoria() : null;
    }

    public enum CategoriaExame {
        CLINICO,
        COMPLEMENTAR,
        LABORATORIAL
    }

    public enum ResultadoExame {
        POSITIVO,
        NEGATIVO,
        INCONCLUSIVO
    }

    public enum TipoExame {

        RESPOSTA_ESTIMULO_DORO("Resposta ao Estímulo Doloroso", CategoriaExame.CLINICO),
        REFLEXO_PUPILAR("Reflexo Pupilar", CategoriaExame.CLINICO),
        REFLEXO_CORNEAL("Reflexo Corneal", CategoriaExame.CLINICO),
        REFLEXO_VESTIBULO_OCULAR("Reflexo Vestibulo-Ocular", CategoriaExame.CLINICO),
        REFLEXO_TOSSE("Reflexo de Tosse", CategoriaExame.CLINICO),
        APNEIA_TEST("Teste de Apneia", CategoriaExame.CLINICO),

        ANGIOGRAFIA_CEREBRAL("Angiografia Cerebral", CategoriaExame.COMPLEMENTAR),
        TOMOGRAFIA_CRANIO("Tomografia de Crânio", CategoriaExame.COMPLEMENTAR),
        ELETROENCEFALOGRAMA("EEG", CategoriaExame.COMPLEMENTAR),

        GASOMETRIA_ARTERIAL("Gasometria", CategoriaExame.LABORATORIAL),
        HEMOGRAMA("Hemograma", CategoriaExame.LABORATORIAL),
        TIPAGEM_SANGUINEA("Tipagem Sanguínea", CategoriaExame.LABORATORIAL),
        SOROLOGIA_HIV("HIV", CategoriaExame.LABORATORIAL);

        private final String label;
        private final CategoriaExame categoria;

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
}
