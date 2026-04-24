package back.backend.mapper;

import back.backend.dto.UsuarioRequestDTO;
import back.backend.model.CentralTransplantes;
import back.backend.model.Hospital;
import back.backend.model.Role;
import back.backend.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", expression = "java(toRole(dto.getRole()))")
    @Mapping(target = "hospital", expression = "java(toHospital(dto.getHospitalId()))")
    @Mapping(target = "centralTransplantes", expression = "java(toCentral(dto.getCentralTransplantesId()))")
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    @Mapping(target = "senha", source = "senha")
    Usuario toEntity(UsuarioRequestDTO dto);

    default Role toRole(String role) {
        if (role == null || role.isBlank()) {
            return null;
        }
        return Role.valueOf(role.toUpperCase());
    }

    default Hospital toHospital(Long hospitalId) {
        if (hospitalId == null) {
            return null;
        }
        Hospital hospital = new Hospital();
        hospital.setId(hospitalId);
        return hospital;
    }

    default CentralTransplantes toCentral(Long centralId) {
        if (centralId == null) {
            return null;
        }
        CentralTransplantes central = new CentralTransplantes();
        central.setId(centralId);
        return central;
    }
}