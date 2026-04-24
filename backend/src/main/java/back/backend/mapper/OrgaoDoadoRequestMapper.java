package back.backend.mapper;

import back.backend.dto.OrgaoDoadoRequestDTO;
import back.backend.model.OrgaoDoado;
import back.backend.model.ProtocoloME;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrgaoDoadoRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "protocoloME", expression = "java(toProtocolo(dto.getProtocoloId()))")
    @Mapping(target = "status", expression = "java(dto.getStatus() != null ? OrgaoDoado.StatusOrgaoDoado.valueOf(dto.getStatus().toUpperCase()) : null)")
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    @Mapping(target = "dataArmazenamento", ignore = true)
    @Mapping(target = "dataImplantacao", ignore = true)
    @Mapping(target = "dataDescarte", ignore = true)
    OrgaoDoado toEntity(OrgaoDoadoRequestDTO dto);

    default ProtocoloME toProtocolo(Long protocoloId) {
        if (protocoloId == null) {
            return null;
        }

        ProtocoloME protocolo = new ProtocoloME();
        protocolo.setId(protocoloId);
        return protocolo;
    }
}