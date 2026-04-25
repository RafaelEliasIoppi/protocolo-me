package back.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "anexo_documento")
public class AnexoDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // RELACIONAMENTOS (CORRETO)
    // =========================

    @ManyToOne
    @JoinColumn(name = "exame_me_id")
    @JsonIgnoreProperties({"protocoloME"})
    private ExameME exameME;

    @ManyToOne
    @JoinColumn(name = "protocolo_me_id")
    @JsonIgnoreProperties({"exames", "paciente", "centralTransplantes"})
    private ProtocoloME protocoloME;

    // =========================
    // DADOS DO ARQUIVO
    // =========================

    @Column(nullable = false)
    private String nomeArquivo;

    @Column(nullable = false)
    private String caminhoArquivo;

    @Column(nullable = false)
    private String tipoMime;

    @Column(nullable = false)
    private Long tamanhoBytes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAnexo tipoAnexo;

    private String descricao;
    private String uploadPor;

    // =========================
    // CONTROLE
    // =========================

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataUpload;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    private LocalDateTime dataAtualizacao;

    // =========================
    // LIFECYCLE
    // =========================

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataUpload = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

    // =========================
    // ENUM
    // =========================

    public enum TipoAnexo {
        EXAME,
        ENTREVISTA,
        DOCUMENTO_GERAL
    }

    // =========================
    // GETTERS E SETTERS
    // =========================

    public Long getId() { return id; }

    public ExameME getExameME() { return exameME; }
    public void setExameME(ExameME exameME) { this.exameME = exameME; }

    public ProtocoloME getProtocoloME() { return protocoloME; }
    public void setProtocoloME(ProtocoloME protocoloME) { this.protocoloME = protocoloME; }

    public String getNomeArquivo() { return nomeArquivo; }
    public void setNomeArquivo(String nomeArquivo) { this.nomeArquivo = nomeArquivo; }

    public String getCaminhoArquivo() { return caminhoArquivo; }
    public void setCaminhoArquivo(String caminhoArquivo) { this.caminhoArquivo = caminhoArquivo; }

    public String getTipoMime() { return tipoMime; }
    public void setTipoMime(String tipoMime) { this.tipoMime = tipoMime; }

    public Long getTamanhoBytes() { return tamanhoBytes; }
    public void setTamanhoBytes(Long tamanhoBytes) { this.tamanhoBytes = tamanhoBytes; }

    public TipoAnexo getTipoAnexo() { return tipoAnexo; }
    public void setTipoAnexo(TipoAnexo tipoAnexo) { this.tipoAnexo = tipoAnexo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getUploadPor() { return uploadPor; }
    public void setUploadPor(String uploadPor) { this.uploadPor = uploadPor; }

    public LocalDateTime getDataUpload() { return dataUpload; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
}
