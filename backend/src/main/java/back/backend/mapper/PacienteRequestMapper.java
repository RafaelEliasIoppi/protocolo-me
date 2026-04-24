package back.backend.mapper;

import back.backend.dto.PacienteRequestDTO;
import back.backend.model.Hospital;
import back.backend.model.Paciente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PacienteRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "genero", expression = "java(dto.getGenero() != null ? Paciente.Genero.valueOf(dto.getGenero().toUpperCase()) : null)")
    @Mapping(target = "hospital", expression = "java(toHospital(dto.getHospitalId()))")
    @Mapping(target = "status", expression = "java(toStatus(dto.getStatus()))")
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    @Mapping(target = "protocolosME", ignore = true)
    @Mapping(target = "examesEmProtocolo", ignore = true)
    Paciente toEntity(PacienteRequestDTO dto);

    default Hospital toHospital(Long hospitalId) {
        if (hospitalId == null) {
            return null;
        }

        Hospital hospital = new Hospital();
        hospital.setId(hospitalId);
        return hospital;
    }

    default Paciente.StatusPaciente toStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }

        return Paciente.StatusPaciente.valueOf(status.toUpperCase());
    }
}