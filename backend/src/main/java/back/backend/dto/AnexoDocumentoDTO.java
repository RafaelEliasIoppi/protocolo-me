package back.backend.dto;

import back.backend.model.AnexoDocumento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

    // ---------------- CONVERSÃO ----------------

    public static AnexoDocumentoDTO fromEntity(AnexoDocumento entity) {

        if (entity == null) return null;

        return new AnexoDocumentoDTO(
                entity.getId(),
                entity.getNomeArquivo(),
                entity.getTipoMime(),
                entity.getTamanhoBytes(),
                entity.getTipoAnexo(),
                entity.getExameMEId(),
                entity.getProtocoloMEId(),
                entity.getDescricao(),
                entity.getUploadPor(),
                entity.getDataUpload(),
                entity.getDataCriacao()
        );
    }
}