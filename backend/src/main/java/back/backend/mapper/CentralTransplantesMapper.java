package back.backend.mapper;

import back.backend.dto.CentralTransplantesDTO;
import back.backend.dto.CentralTransplantesRequestDTO;
import back.backend.model.CentralTransplantes;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CentralTransplantesMapper {

    CentralTransplantesDTO toDTO(CentralTransplantes entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "statusOperacional", ignore = true)
    @Mapping(target = "hospitaisNotificantes", ignore = true)
    @Mapping(target = "hospitaisParceados", ignore = true)
    @Mapping(target = "protocolosME", ignore = true)
    @Mapping(target = "doacoes", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    CentralTransplantes toEntity(CentralTransplantesRequestDTO dto);
}
