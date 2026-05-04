package back.backend.mapper;

import back.backend.dto.ExameRequestDTO;
import back.backend.model.ExameME;
import back.backend.model.ProtocoloME;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ExameRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "protocoloME", expression = "java(toProtocolo(dto.getProtocoloId()))")
    @Mapping(target = "tipoExame", expression = "java(dto.getTipoExame() != null ? ExameME.TipoExame.valueOf(dto.getTipoExame().toUpperCase()) : null)")
    @Mapping(target = "resultado", expression = "java(toResultado(dto.getResultado(), dto.getResultadoPositivo()))")
    @Mapping(target = "statusValidacao", ignore = true)
    @Mapping(target = "validadoPor", ignore = true)
    @Mapping(target = "dataValidacao", ignore = true)
    @Mapping(target = "observacoesValidacao", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    ExameME toEntity(ExameRequestDTO dto);

    default ExameME.ResultadoExame toResultado(String resultado, Boolean resultadoPositivo) {
        if (resultado != null && !resultado.isBlank()) {
            return ExameME.ResultadoExame.valueOf(resultado.trim().toUpperCase());
        }
        if (resultadoPositivo == null) {
            return null;
        }
        return resultadoPositivo
                ? ExameME.ResultadoExame.POSITIVO
                : ExameME.ResultadoExame.NEGATIVO;
    }

    default ProtocoloME toProtocolo(Long protocoloId) {
        if (protocoloId == null) {
            return null;
        }

        ProtocoloME protocolo = new ProtocoloME();
        protocolo.setId(protocoloId);
        return protocolo;
    }
}
