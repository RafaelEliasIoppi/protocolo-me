package back.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "central_transplantes")
public class CentralTransplantes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false)
    private String cnpj;

    @Column(nullable = false)
    private String endereco;

    @Column(nullable = false)
    private String cidade;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private String telefone;

    private String telefonePlantao;

    @Column(nullable = false)
    private String email;

    private String emailPlantao;

    @Column(nullable = false)
    private String coordenador;

    private String telefoneCoordenador;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCentral statusOperacional = StatusCentral.ATIVO;

    private Integer capacidadeProcessamento;

    @Column(length = 2000)
    private String especialidadesOrgaos;

    /**
     * 🔥 MELHORIA:
     * Hospitais que notificam a central
     */
    @ManyToMany
    @JoinTable(
            name = "central_hospitais",
            joinColumns = @JoinColumn(name = "central_id"),
            inverseJoinColumns = @JoinColumn(name = "hospital_id")
    )
    private List<Hospital> hospitaisNotificantes;

    /**
     * Protocolos acompanhados pela central
     */
    @OneToMany(mappedBy = "centralTransplantes", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ProtocoloME> protocolosME;

    /**
     * 🔥 NOVO: vínculo com DOAÇÃO
     */
    @OneToMany(mappedBy = "centralTransplantes", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Doacao> doacoes;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

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
    // ENUM
    // =========================

    public enum StatusCentral {
        ATIVO("Ativo", "Central operacional"),
        INATIVO("Inativo", "Central não operacional"),
        PLANTAO("Plantão", "Em plantão"),
        MANUTENCAO("Manutenção", "Em manutenção");

        private String label;
        private String descricao;

        StatusCentral(String label, String descricao) {
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

    // =========================
    // GETTERS E SETTERS
    // =========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getCidade() {
        return cidade;
    }

    public String getEstado() {
        return estado;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getTelefonePlantao() {
        return telefonePlantao;
    }

    public String getEmail() {
        return email;
    }

    public String getEmailPlantao() {
        return emailPlantao;
    }

    public String getCoordenador() {
        return coordenador;
    }

    public String getTelefoneCoordenador() {
        return telefoneCoordenador;
    }

    public StatusCentral getStatusOperacional() {
        return statusOperacional;
    }

    public Integer getCapacidadeProcessamento() {
        return capacidadeProcessamento;
    }

    public String getEspecialidadesOrgaos() {
        return especialidadesOrgaos;
    }

    public List<Hospital> getHospitaisNotificantes() {
        return hospitaisNotificantes;
    }

    public List<Hospital> getHospitaisParceados() {
        return hospitaisNotificantes;
    }

    public List<ProtocoloME> getProtocolosME() {
        return protocolosME;
    }

    public List<Doacao> getDoacoes() {
        return doacoes;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setTelefonePlantao(String telefonePlantao) {
        this.telefonePlantao = telefonePlantao;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEmailPlantao(String emailPlantao) {
        this.emailPlantao = emailPlantao;
    }

    public void setCoordenador(String coordenador) {
        this.coordenador = coordenador;
    }

    public void setTelefoneCoordenador(String telefoneCoordenador) {
        this.telefoneCoordenador = telefoneCoordenador;
    }

    public void setStatusOperacional(StatusCentral statusOperacional) {
        this.statusOperacional = statusOperacional;
    }

    public void setCapacidadeProcessamento(Integer capacidadeProcessamento) {
        this.capacidadeProcessamento = capacidadeProcessamento;
    }

    public void setEspecialidadesOrgaos(String especialidadesOrgaos) {
        this.especialidadesOrgaos = especialidadesOrgaos;
    }

    public void setHospitaisNotificantes(List<Hospital> hospitaisNotificantes) {
        this.hospitaisNotificantes = hospitaisNotificantes;
    }

    public void setHospitaisParceados(List<Hospital> hospitaisParceados) {
        this.hospitaisNotificantes = hospitaisParceados;
    }

    public void setProtocolosME(List<ProtocoloME> protocolosME) {
        this.protocolosME = protocolosME;
    }

    public void setDoacoes(List<Doacao> doacoes) {
        this.doacoes = doacoes;
    }
}
