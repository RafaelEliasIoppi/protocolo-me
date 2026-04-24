package back.backend.mapper;

import back.backend.dto.HospitalDTO;
import back.backend.model.Hospital;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HospitalMapper {

    @Mapping(target = "status", expression = "java(entity.getStatus() != null ? entity.getStatus().name() : null)")
    HospitalDTO toDTO(Hospital entity);
}