package back.backend.mapper;

import back.backend.dto.AnexoDocumentoDTO;
import back.backend.model.AnexoDocumento;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface AnexoDocumentoMapper {

    AnexoDocumentoDTO toDTO(AnexoDocumento entity);
}