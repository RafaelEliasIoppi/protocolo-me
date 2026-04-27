package back.backend.mapper;

import back.backend.dto.ExameMEDTO;
import back.backend.model.ExameME;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ExameMapper {

    @Mapping(target = "protocoloId", expression = "java(entity.getProtocoloME() != null ? entity.getProtocoloME().getId() : null)")
    @Mapping(target = "protocoloNumero", expression = "java(entity.getProtocoloME() != null ? entity.getProtocoloME().getNumeroProtocolo() : null)")
    @Mapping(target = "categoria", expression = "java(entity.getCategoria() != null ? entity.getCategoria().name() : null)")
    @Mapping(target = "tipoExame", expression = "java(entity.getTipoExame() != null ? entity.getTipoExame().name() : null)")
    @Mapping(target = "resultado", expression = "java(entity.getResultado() != null ? entity.getResultado().name() : null)")
    @Mapping(target = "resultadoPositivo", expression = "java(entity.getResultado() != null ? entity.getResultado() == back.backend.model.ExameME.ResultadoExame.POSITIVO : null)")
    ExameMEDTO toDTO(ExameME entity);
}
