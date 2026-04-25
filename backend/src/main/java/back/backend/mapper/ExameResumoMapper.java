package back.backend.mapper;

import back.backend.dto.ExameResumoDTO;
import back.backend.service.ExameMEService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ExameResumoMapper {

    @Mapping(source = "examesClinicos", target = "examesClinicos")
    ExameResumoDTO toDTO(ExameMEService.ExameResumo resumo);
}
