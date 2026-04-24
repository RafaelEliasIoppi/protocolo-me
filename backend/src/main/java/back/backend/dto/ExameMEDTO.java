package back.backend.dto;

import back.backend.model.ExameME;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
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

    // =========================
    // ENTITY -> DTO
    // =========================
    public static ExameMEDTO fromEntity(ExameME entity) {
        if (entity == null) return null;

        return ExameMEDTO.builder()
                .id(entity.getId())
                .protocoloId(getProtocoloId(entity))
                .protocoloNumero(getProtocoloNumero(entity))
                .categoria(getEnumName(entity.getCategoria()))
                .tipoExame(getEnumName(entity.getTipoExame()))
                .descricao(entity.getDescricao())
                .resultado(entity.getResultado())
                .resultadoPositivo(entity.getResultado_positivo())
                .dataRealizacao(entity.getDataRealizacao())
                .responsavel(entity.getResponsavel())
                .observacoes(entity.getObservacoes())
                .dataCriacao(entity.getDataCriacao())
                .dataAtualizacao(entity.getDataAtualizacao())
                .build();
    }

    // =========================
    // HELPERS
    // =========================
    private static Long getProtocoloId(ExameME e) {
        return (e.getProtocoloME() != null) ? e.getProtocoloME().getId() : null;
    }

    private static String getProtocoloNumero(ExameME e) {
        return (e.getProtocoloME() != null) ? e.getProtocoloME().getNumeroProtocolo() : null;
    }

    private static String getEnumName(Enum<?> e) {
        return e != null ? e.name() : null;
    }

    // =========================
    // (OPCIONAL) DTO -> ENTITY
    // =========================
    public ExameME toEntity() {
        ExameME entity = new ExameME();

        entity.setId(this.id);
        entity.setDescricao(this.descricao);
        entity.setResultado(this.resultado);
        entity.setResultado_positivo(this.resultadoPositivo);
        entity.setDataRealizacao(this.dataRealizacao);
        entity.setResponsavel(this.responsavel);
        entity.setObservacoes(this.observacoes);

        // enums (cuidado com null)
        if (this.categoria != null) {
            entity.setCategoria(ExameME.CategoriaExame.valueOf(this.categoria));
        }

        if (this.tipoExame != null) {
            entity.setTipoExame(ExameME.TipoExame.valueOf(this.tipoExame));
        }

        return entity;
    }
}