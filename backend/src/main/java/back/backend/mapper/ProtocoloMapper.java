package back.backend.mapper;

import back.backend.dto.OrgaoDoadoDTO;
import back.backend.dto.ProtocoloMEDTO;
import back.backend.model.CentralTransplantes;
import back.backend.model.OrgaoDoado;
import back.backend.model.Paciente;
import back.backend.model.ProtocoloME;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = "spring", uses = { OrgaoDoadoMapper.class,
        ExameMapper.class }, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ProtocoloMapper {

    @Mapping(target = "status", expression = "java(entity.getStatus() != null ? entity.getStatus().name() : null)")
    @Mapping(target = "centralTransplantesId", expression = "java(getCentralTransplantesId(entity.getCentralTransplantes()))")
    @Mapping(target = "centralTransplantesNome", expression = "java(getCentralTransplantesNome(entity.getCentralTransplantes()))")
    @Mapping(target = "paciente", expression = "java(toPacienteResumo(entity.getPaciente()))")
    @Mapping(target = "exames", source = "exames")
    @Mapping(target = "orgaosDoados", source = "doacao.orgaos")
    ProtocoloMEDTO toDTO(ProtocoloME entity);

    default Long getCentralTransplantesId(CentralTransplantes centralTransplantes) {
        return centralTransplantes != null ? centralTransplantes.getId() : null;
    }

    default String getCentralTransplantesNome(CentralTransplantes centralTransplantes) {
        return centralTransplantes != null ? centralTransplantes.getNome() : null;
    }

    default ProtocoloMEDTO.PacienteResumoDTO toPacienteResumo(Paciente paciente) {
        if (paciente == null) {
            return null;
        }

        Long hospitalId = paciente.getHospital() != null ? paciente.getHospital().getId() : null;
        String hospitalNome = paciente.getHospital() != null ? paciente.getHospital().getNome() : null;

        return new ProtocoloMEDTO.PacienteResumoDTO(
                paciente.getId(),
                paciente.getNome(),
                paciente.getCpf(),
                hospitalId,
                hospitalNome,
                paciente.getLeito(),
                paciente.getStatusEntrevistaFamiliar());
    }
}
