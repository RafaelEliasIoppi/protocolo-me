package back.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "hospital")
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true, length = 14)
    private String cnpj;

    @Column(nullable = false)
    private String endereco;

    @Column(nullable = false)
    private String cidade;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private String telefone;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusHospital status = StatusHospital.ATIVO;

    // 🔥 NOVO: tipo do hospital
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoHospital tipo = TipoHospital.NOTIFICANTE;

    @Column
    private String responsavelMedico;

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

    // ENUMS

    public enum StatusHospital {
        ATIVO,
        INATIVO,
        MANUTENCAO,
        SUSPENSAO
    }

    public enum TipoHospital {
        NOTIFICANTE,
        TRANSPLANTADOR,
        AMBOS
    }

    // Getters e Setters

    public Long getId() { return id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public StatusHospital getStatus() { return status; }
    public void setStatus(StatusHospital status) { this.status = status; }

    public TipoHospital getTipo() { return tipo; }
    public void setTipo(TipoHospital tipo) { this.tipo = tipo; }

    public String getResponsavelMedico() { return responsavelMedico; }
    public void setResponsavelMedico(String responsavelMedico) { this.responsavelMedico = responsavelMedico; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
}
