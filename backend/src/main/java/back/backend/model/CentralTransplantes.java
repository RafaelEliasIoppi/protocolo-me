package back.backend.model;

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

    @Column
    private String telefonePlantao;

    @Column(nullable = false)
    private String email;

    @Column
    private String emailPlantao;

    @Column(nullable = false)
    private String coordenador;

    @Column
    private String telefoneCoordenador;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCentral statusOperacional = StatusCentral.ATIVO;

    @Column
    private Integer capacidadeProcessamento;

    @Column
    private String especialidadesOrgaos;

    @ManyToMany
    @JoinTable(
            name = "central_hospitais",
            joinColumns = @JoinColumn(name = "central_id"),
            inverseJoinColumns = @JoinColumn(name = "hospital_id")
    )
    private List<Hospital> hospitaisParceados;

    @OneToMany(mappedBy = "centralTransplantes", cascade = CascadeType.ALL)
    private List<ProtocoloME> protocolosME;

    @OneToMany(mappedBy = "centralTransplantes")
    private List<Usuario> usuarios;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    // Construtores
    public CentralTransplantes() {}

    public CentralTransplantes(Long id, String nome, String cnpj, String endereco, String cidade, String estado, 
                              String telefone, String telefonePlantao, String email, String emailPlantao, 
                              String coordenador, String telefoneCoordenador, StatusCentral statusOperacional, 
                              Integer capacidadeProcessamento, String especialidadesOrgaos, 
                              List<Hospital> hospitaisParceados, List<ProtocoloME> protocolosME, 
                              List<Usuario> usuarios, LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) {
        this.id = id;
        this.nome = nome;
        this.cnpj = cnpj;
        this.endereco = endereco;
        this.cidade = cidade;
        this.estado = estado;
        this.telefone = telefone;
        this.telefonePlantao = telefonePlantao;
        this.email = email;
        this.emailPlantao = emailPlantao;
        this.coordenador = coordenador;
        this.telefoneCoordenador = telefoneCoordenador;
        this.statusOperacional = statusOperacional;
        this.capacidadeProcessamento = capacidadeProcessamento;
        this.especialidadesOrgaos = especialidadesOrgaos;
        this.hospitaisParceados = hospitaisParceados;
        this.protocolosME = protocolosME;
        this.usuarios = usuarios;
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getTelefonePlantao() {
        return telefonePlantao;
    }

    public void setTelefonePlantao(String telefonePlantao) {
        this.telefonePlantao = telefonePlantao;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailPlantao() {
        return emailPlantao;
    }

    public void setEmailPlantao(String emailPlantao) {
        this.emailPlantao = emailPlantao;
    }

    public String getCoordenador() {
        return coordenador;
    }

    public void setCoordenador(String coordenador) {
        this.coordenador = coordenador;
    }

    public String getTelefoneCoordenador() {
        return telefoneCoordenador;
    }

    public void setTelefoneCoordenador(String telefoneCoordenador) {
        this.telefoneCoordenador = telefoneCoordenador;
    }

    public StatusCentral getStatusOperacional() {
        return statusOperacional;
    }

    public void setStatusOperacional(StatusCentral statusOperacional) {
        this.statusOperacional = statusOperacional;
    }

    public Integer getCapacidadeProcessamento() {
        return capacidadeProcessamento;
    }

    public void setCapacidadeProcessamento(Integer capacidadeProcessamento) {
        this.capacidadeProcessamento = capacidadeProcessamento;
    }

    public String getEspecialidadesOrgaos() {
        return especialidadesOrgaos;
    }

    public void setEspecialidadesOrgaos(String especialidadesOrgaos) {
        this.especialidadesOrgaos = especialidadesOrgaos;
    }

    public List<Hospital> getHospitaisParceados() {
        return hospitaisParceados;
    }

    public void setHospitaisParceados(List<Hospital> hospitaisParceados) {
        this.hospitaisParceados = hospitaisParceados;
    }

    public List<ProtocoloME> getProtocolosME() {
        return protocolosME;
    }

    public void setProtocolosME(List<ProtocoloME> protocolosME) {
        this.protocolosME = protocolosME;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
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
    }

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

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
}
