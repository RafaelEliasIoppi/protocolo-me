package back.backend.mapper;

import back.backend.dto.OrgaoDoadoDTO;
import back.backend.model.OrgaoDoado;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface OrgaoDoadoMapper {

    @Mapping(target = "protocoloId", expression = "java(entity.getProtocoloME() != null ? entity.getProtocoloME().getId() : null)")
    @Mapping(target = "numeroProtocolo", expression = "java(entity.getProtocoloME() != null ? entity.getProtocoloME().getNumeroProtocolo() : null)")
    @Mapping(target = "status", expression = "java(entity.getStatus() != null ? entity.getStatus().name() : null)")
    OrgaoDoadoDTO toDTO(OrgaoDoado entity);
}