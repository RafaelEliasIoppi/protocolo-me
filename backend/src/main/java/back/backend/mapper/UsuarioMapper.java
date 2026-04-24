package back.backend.mapper;

import back.backend.dto.UsuarioDTO;
import back.backend.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(target = "role", expression = "java(entity.getRole() != null ? entity.getRole().name() : null)")
    @Mapping(target = "hospitalId", expression = "java(entity.getHospital() != null ? entity.getHospital().getId() : null)")
    @Mapping(target = "hospitalNome", expression = "java(entity.getHospital() != null ? entity.getHospital().getNome() : null)")
    @Mapping(target = "centralTransplantesId", expression = "java(entity.getCentralTransplantes() != null ? entity.getCentralTransplantes().getId() : null)")
    @Mapping(target = "centralTransplantesNome", expression = "java(entity.getCentralTransplantes() != null ? entity.getCentralTransplantes().getNome() : null)")
    UsuarioDTO toDTO(Usuario entity);
}