package back.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    @Column
    private String telefoneResponsavel;

    @Column
    private String emailResponsavel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPaciente status = StatusPaciente.INTERNADO;

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"paciente", "centralTransplantes", "exames"})
    private List<ProtocoloME> protocolosME;

    // Exames adicionados quando em protocolo ME
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

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public Genero getGenero() { return genero; }
    public void setGenero(Genero genero) { this.genero = genero; }

    public String getHospitalOrigem() { return hospitalOrigem; }
    public void setHospitalOrigem(String hospitalOrigem) { this.hospitalOrigem = hospitalOrigem; }

    public Hospital getHospital() { return hospital; }
    public void setHospital(Hospital hospital) { this.hospital = hospital; }

    public String getLeito() { return leito; }
    public void setLeito(String leito) { this.leito = leito; }

    public LocalDate getDataInternacao() { return dataInternacao; }
    public void setDataInternacao(LocalDate dataInternacao) { this.dataInternacao = dataInternacao; }

    public String getDiagnosticoPrincipal() { return diagnosticoPrincipal; }
    public void setDiagnosticoPrincipal(String diagnosticoPrincipal) { this.diagnosticoPrincipal = diagnosticoPrincipal; }

    public String getHistoricoMedico() { return historicoMedico; }
    public void setHistoricoMedico(String historicoMedico) { this.historicoMedico = historicoMedico; }

    public String getNomeResponsavel() { return nomeResponsavel; }
    public void setNomeResponsavel(String nomeResponsavel) { this.nomeResponsavel = nomeResponsavel; }

    public String getTelefoneResponsavel() { return telefoneResponsavel; }
    public void setTelefoneResponsavel(String telefoneResponsavel) { this.telefoneResponsavel = telefoneResponsavel; }

    public String getEmailResponsavel() { return emailResponsavel; }
    public void setEmailResponsavel(String emailResponsavel) { this.emailResponsavel = emailResponsavel; }

    public StatusPaciente getStatus() { return status; }
    public void setStatus(StatusPaciente status) { this.status = status; }

    public List<ProtocoloME> getProtocolosME() { return protocolosME; }
    public void setProtocolosME(List<ProtocoloME> protocolosME) { this.protocolosME = protocolosME; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }

    public List<ExameME> getExamesEmProtocolo() { return examesEmProtocolo; }
    public void setExamesEmProtocolo(List<ExameME> examesEmProtocolo) { this.examesEmProtocolo = examesEmProtocolo; }

}
