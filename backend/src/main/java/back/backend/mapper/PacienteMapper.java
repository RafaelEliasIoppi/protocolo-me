package back.backend.mapper;

import back.backend.dto.PacienteDTO;
import back.backend.model.Paciente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface PacienteMapper {

    @Mapping(target = "genero", expression = "java(entity.getGenero() != null ? entity.getGenero().name() : null)")
    @Mapping(target = "hospitalId", expression = "java(entity.getHospital() != null ? entity.getHospital().getId() : null)")
    @Mapping(target = "hospitalNome", expression = "java(entity.getHospital() != null ? entity.getHospital().getNome() : null)")
    @Mapping(target = "dataInternacao", source = "dataInternacao")
    @Mapping(target = "status", expression = "java(entity.getStatus() != null ? entity.getStatus().name() : null)")
    @Mapping(target = "statusEntrevistaFamiliar",expression = "java(entity.getStatusEntrevistaFamiliar())")
    @Mapping(target = "observacoesEntrevistaFamiliar", ignore = true)
    @Mapping(target = "dataEntrevistaFamiliar", ignore = true)
    PacienteDTO toDTO(Paciente entity);
}
