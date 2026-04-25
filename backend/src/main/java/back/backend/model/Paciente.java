package back.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "paciente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nome;

    @Column(nullable = false, unique = true)
    @NotBlank
    @Size(min = 11, max = 11)
    private String cpf;

    @Column(nullable = false)
    private LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Genero genero;

    private String hospitalOrigem;

    @ManyToOne(optional = false)
    @JoinColumn(name = "hospital_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Hospital hospital;

    private String leito;

    private LocalDate dataInternacao;

    @Column(columnDefinition = "TEXT")
    private String diagnosticoPrincipal;

    @Column(columnDefinition = "TEXT")
    private String historicoMedico;

    // Responsável
    private String nomeResponsavel;
    private String telefoneResponsavel;
    private String emailResponsavel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPaciente status = StatusPaciente.INTERNADO;

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"paciente", "centralTransplantes", "exames"})
    private List<ProtocoloME> protocolosME;

    @Transient
    private List<ExameME> examesEmProtocolo;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(nullable = false)
    private LocalDateTime dataAtualizacao;

    @PrePersist
    private void prePersist() {
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    public enum Genero {
        MASCULINO, FEMININO, OUTRO
    }

    public enum StatusPaciente {
        PRE_INTERNACAO,
        INTERNADO,
        EM_PROTOCOLO_ME,
        APTO_TRANSPLANTE,
        NAO_APTO,
        RECUSADO,
        EXODO
    }
}
