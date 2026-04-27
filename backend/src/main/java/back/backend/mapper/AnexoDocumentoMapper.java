package back.backend.mapper;

import back.backend.dto.AnexoDocumentoDTO;
import back.backend.model.AnexoDocumento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface AnexoDocumentoMapper {

    @Mapping(target = "tipoAnexo", expression = "java(entity.getTipoAnexo() != null ? entity.getTipoAnexo().name() : null)")
    @Mapping(target = "exameMEId", expression = "java(entity.getExameME() != null ? entity.getExameME().getId() : null)")
    @Mapping(target = "protocoloMEId", expression = "java(entity.getProtocoloME() != null ? entity.getProtocoloME().getId() : null)")
    AnexoDocumentoDTO toDTO(AnexoDocumento entity);
}
