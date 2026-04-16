package back.backend.model;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "paciente")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false)
    private LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Genero genero;

    @Column
    private String hospitalOrigem;

    @ManyToOne(optional = false)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @Column
    private String leito;

    @Column
    private LocalDate dataInternacao;

    @Column(columnDefinition = "TEXT")
    private String diagnosticoPrincipal;

    @Column(columnDefinition = "TEXT")
    private String historicoMedico;

    @Column
    private String nomeResponsavel;

    @Column(name = "telefono_responsavel")
    private String telefoneResponsavel;

    @Column
    private String emailResponsavel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPaciente status = StatusPaciente.INTERNADO;

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProtocoloME> protocolosME;

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

    // Enums
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
