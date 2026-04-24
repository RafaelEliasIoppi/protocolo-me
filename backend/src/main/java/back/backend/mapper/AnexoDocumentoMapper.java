package back.backend.mapper;

import back.backend.dto.AnexoDocumentoDTO;
import back.backend.model.AnexoDocumento;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AnexoDocumentoMapper {

    AnexoDocumentoDTO toDTO(AnexoDocumento entity);
}