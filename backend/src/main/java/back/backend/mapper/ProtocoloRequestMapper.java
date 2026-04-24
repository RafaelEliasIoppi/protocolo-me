package back.backend.mapper;

import back.backend.dto.ProtocoloUpdateRequestDTO;
import back.backend.model.ProtocoloME;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ProtocoloRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paciente", ignore = true)
    @Mapping(target = "centralTransplantes", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "dataNotificacao", ignore = true)
    @Mapping(target = "dataConfirmacaoME", ignore = true)
    @Mapping(target = "dataSaidaHospital", ignore = true)
    @Mapping(target = "testeClinico1Realizado", ignore = true)
    @Mapping(target = "dataTesteClinico1", ignore = true)
    @Mapping(target = "testeClinico2Realizado", ignore = true)
    @Mapping(target = "dataTesteClinico2", ignore = true)
    @Mapping(target = "testesComplementaresRealizados", ignore = true)
    @Mapping(target = "testesComplementares", ignore = true)
    @Mapping(target = "dataTesteComplementar", ignore = true)
    @Mapping(target = "familiaNotificada", ignore = true)
    @Mapping(target = "dataNotificacaoFamilia", ignore = true)
    @Mapping(target = "autopsiaAutorizada", ignore = true)
    @Mapping(target = "preservacaoOrgaos", ignore = true)
    @Mapping(target = "dataPreservacao", ignore = true)
    @Mapping(target = "relatorioFinalEditavel", ignore = true)
    @Mapping(target = "relatorioFinalAtualizadoPor", ignore = true)
    @Mapping(target = "relatorioFinalAtualizadoEm", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    @Mapping(target = "hospitalOrigem", ignore = true)
    ProtocoloME toEntity(ProtocoloUpdateRequestDTO dto);
}