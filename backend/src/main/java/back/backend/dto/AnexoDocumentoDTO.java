package back.backend.dto;

import java.time.LocalDateTime;

public class AnexoDocumentoDTO {

    private Long id;
    private String nomeArquivo;
    private String tipoMime;
    private Long tamanhoBytes;
    private String tipoAnexo;

    private Long exameMEId;
    private Long protocoloMEId;

    private String descricao;
    private String uploadPor;

    private LocalDateTime dataUpload;
    private LocalDateTime dataCriacao;

    public AnexoDocumentoDTO() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomeArquivo() { return nomeArquivo; }
    public void setNomeArquivo(String nomeArquivo) { this.nomeArquivo = nomeArquivo; }

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