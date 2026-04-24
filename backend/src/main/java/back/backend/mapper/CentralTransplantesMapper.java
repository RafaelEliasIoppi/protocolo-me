package back.backend.mapper;

import back.backend.dto.CentralTransplantesDTO;
import back.backend.model.CentralTransplantes;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CentralTransplantesMapper {

    CentralTransplantesDTO toDTO(CentralTransplantes entity);

    CentralTransplantes toEntity(CentralTransplantesDTO dto);
}