package back.backend.dto;

import back.backend.model.ExameME;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExameMEDTO {

    private Long id;
    private Long protocoloId;
    private String protocoloNumero;
    private String categoria;
    private String tipoExame;
    private String descricao;
    private String resultado;
    private Boolean resultadoPositivo;
    private LocalDateTime dataRealizacao;
    private String responsavel;
    private String observacoes;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public static ExameMEDTO fromEntity(ExameME entity) {
        if (entity == null) {
            return null;
        }

        Long protocoloId = null;
        String protocoloNumero = null;
        if (entity.getProtocoloME() != null) {
            protocoloId = entity.getProtocoloME().getId();
            protocoloNumero = entity.getProtocoloME().getNumeroProtocolo();
        }

        return new ExameMEDTO(
            entity.getId(),
            protocoloId,
            protocoloNumero,
            entity.getCategoria() != null ? entity.getCategoria().name() : null,
            entity.getTipoExame() != null ? entity.getTipoExame().name() : null,
            entity.getDescricao(),
            entity.getResultado(),
            entity.getResultado_positivo(),
            entity.getDataRealizacao(),
            entity.getResponsavel(),
            entity.getObservacoes(),
            entity.getDataCriacao(),
            entity.getDataAtualizacao()
        );
    }
}