package back.backend.mapper;

import back.backend.dto.CentralTransplantesDTO;
import back.backend.dto.CentralTransplantesRequestDTO;
import back.backend.model.CentralTransplantes;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CentralTransplantesMapper {

    CentralTransplantesDTO toDTO(CentralTransplantes entity);

    CentralTransplantes toEntity(CentralTransplantesRequestDTO dto);
}