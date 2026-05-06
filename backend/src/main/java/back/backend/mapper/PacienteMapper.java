package back.backend.mapper;

import back.backend.dto.PacienteDTO;
import back.backend.dto.ProtocoloResumoDTO;
import back.backend.model.Paciente;
import back.backend.model.ProtocoloME;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    @Mapping(target = "protocolosME", expression = "java(mapProtocolosME(entity.getProtocolosME()))")
    PacienteDTO toDTO(Paciente entity);

    default List<ProtocoloResumoDTO> mapProtocolosME(List<ProtocoloME> protocolos) {
        if (protocolos == null || protocolos.isEmpty()) {
            return new ArrayList<>();
        }

        return protocolos.stream()
                .map(this::mapProtocoloME)
                .collect(Collectors.toList());
    }

    default ProtocoloResumoDTO mapProtocoloME(ProtocoloME protocolo) {
        if (protocolo == null) {
            return null;
        }

        ProtocoloResumoDTO dto = new ProtocoloResumoDTO();
        dto.setId(protocolo.getId());
        dto.setNumeroProtocolo(protocolo.getNumeroProtocolo());
        dto.setStatus(protocolo.getStatus() != null ? protocolo.getStatus().name() : null);
        dto.setHospitalOrigem(protocolo.getHospitalOrigem());
        dto.setDiagnosticoBasico(protocolo.getDiagnosticoBasico());
        dto.setCausaMorte(protocolo.getCausaMorte());
        dto.setObservacoes(protocolo.getObservacoes());
        dto.setMedicoResponsavel(protocolo.getMedicoResponsavel());
        dto.setEnfermeiro(protocolo.getEnfermeiro());
        dto.setOrgaosDisponiveis(protocolo.getOrgaosDisponiveis());
        return dto;
    }
}

