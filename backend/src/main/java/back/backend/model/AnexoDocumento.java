package back.backend.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "anexo_documento")
public class AnexoDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomeArquivo;

    @Column(nullable = false)
    private String caminhoArquivo;

    @Column(nullable = false)
    private String tipoMime;

    @Column(nullable = false)
    private Long tamanhoBytes;

    @Column(nullable = false)
    private String tipoAnexo; // "EXAME" ou "ENTREVISTA"

    @Column
    private Long exameMEId;

    @Column
    private Long protocoloMEId;

    @Column
    private String descricao;

    @Column
    private String uploadPor;

    @Column(name = "data_upload", nullable = false, updatable = false)
    private LocalDateTime dataUpload;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    // Construtores
    public AnexoDocumento() {}

    public AnexoDocumento(String nomeArquivo, String caminhoArquivo, String tipoMime, 
                         Long tamanhoBytes, String tipoAnexo, String descricao, String uploadPor) {
        this.nomeArquivo = nomeArquivo;
        this.caminhoArquivo = caminhoArquivo;
        this.tipoMime = tipoMime;
        this.tamanhoBytes = tamanhoBytes;
        this.tipoAnexo = tipoAnexo;
        this.descricao = descricao;
        this.uploadPor = uploadPor;
    }

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataUpload = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomeArquivo() { return nomeArquivo; }
    public void setNomeArquivo(String nomeArquivo) { this.nomeArquivo = nomeArquivo; }

    public String getCaminhoArquivo() { return caminhoArquivo; }
    public void setCaminhoArquivo(String caminhoArquivo) { this.caminhoArquivo = caminhoArquivo; }

    public String getTipoMime() { return tipoMime; }
    public void setTipoMime(String tipoMime) { this.tipoMime = tipoMime; }

    public Long getTamanhoBytes() { return tamanhoBytes; }
    public void setTamanhoBytes(Long tamanhoBytes) { this.tamanhoBytes = tamanhoBytes; }

    public String getTipoAnexo() { return tipoAnexo; }
    public void setTipoAnexo(String tipoAnexo) { this.tipoAnexo = tipoAnexo; }

    public Long getExameMEId() { return exameMEId; }
    public void setExameMEId(Long exameMEId) { this.exameMEId = exameMEId; }

    public Long getProtocoloMEId() { return protocoloMEId; }
    public void setProtocoloMEId(Long protocoloMEId) { this.protocoloMEId = protocoloMEId; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getUploadPor() { return uploadPor; }
    public void setUploadPor(String uploadPor) { this.uploadPor = uploadPor; }

    public LocalDateTime getDataUpload() { return dataUpload; }
    public void setDataUpload(LocalDateTime dataUpload) { this.dataUpload = dataUpload; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
}
