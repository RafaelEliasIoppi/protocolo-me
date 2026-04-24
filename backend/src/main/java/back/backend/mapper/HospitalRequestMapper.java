package back.backend.mapper;

import back.backend.dto.HospitalRequestDTO;
import back.backend.model.Hospital;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HospitalRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    Hospital toEntity(HospitalRequestDTO dto);
}