package back.backend.mapper;

import back.backend.dto.ExameResumoDTO;
import back.backend.service.ExameMEService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExameResumoMapper {

    @Mapping(source = "exames_Clinicos", target = "examesClinicos")
    ExameResumoDTO toDTO(ExameMEService.ExameResumo resumo);
}