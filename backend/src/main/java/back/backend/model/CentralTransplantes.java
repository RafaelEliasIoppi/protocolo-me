package back.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "central_transplantes")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
