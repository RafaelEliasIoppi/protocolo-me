package back.backend.mapper;

import back.backend.dto.ExameRequestDTO;
import back.backend.model.ExameME;
import back.backend.model.ProtocoloME;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ExameRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "protocoloME", expression = "java(toProtocolo(dto.getProtocoloId()))")
    @Mapping(target = "categoria", expression = "java(dto.getCategoria() != null ? ExameME.CategoriaExame.valueOf(dto.getCategoria().toUpperCase()) : null)")
    @Mapping(target = "tipoExame", expression = "java(dto.getTipoExame() != null ? ExameME.TipoExame.valueOf(dto.getTipoExame().toUpperCase()) : null)")
    @Mapping(target = "resultado_positivo", source = "resultadoPositivo")
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    ExameME toEntity(ExameRequestDTO dto);

    default ProtocoloME toProtocolo(Long protocoloId) {
        if (protocoloId == null) {
            return null;
        }

        ProtocoloME protocolo = new ProtocoloME();
        protocolo.setId(protocoloId);
        return protocolo;
    }
}