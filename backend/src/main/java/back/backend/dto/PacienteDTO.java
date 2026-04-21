package back.backend.dto;

import back.backend.model.Paciente;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PacienteDTO {

    private Long id;
    private String nome;
    private String cpf;
    private LocalDate dataNascimento;
    private String genero;
    private String hospitalOrigem;
    private Long hospitalId;
    private String hospitalNome;
    private String leito;
    private LocalDate dataInternacao;
    private String diagnosticoPrincipal;
    private String historicoMedico;
    private String nomeResponsavel;
    private String telefoneResponsavel;
    private String emailResponsavel;
    private String statusEntrevistaFamiliar;
    private String observacoesEntrevistaFamiliar;
    private LocalDateTime dataEntrevistaFamiliar;
    private String status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private List<ProtocoloMEDTO> protocolosME;

    public static PacienteDTO fromEntity(Paciente entity) {
        if (entity == null) return null;
        PacienteDTO dto = new PacienteDTO();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setCpf(entity.getCpf());
        dto.setDataNascimento(entity.getDataNascimento());
        dto.setGenero(entity.getGenero() != null ? entity.getGenero().name() : null);
        dto.setHospitalOrigem(entity.getHospitalOrigem());
        dto.setLeito(entity.getLeito());
        dto.setDataInternacao(entity.getDataInternacao());
        dto.setDiagnosticoPrincipal(entity.getDiagnosticoPrincipal());
        dto.setHistoricoMedico(entity.getHistoricoMedico());
        dto.setNomeResponsavel(entity.getNomeResponsavel());
        dto.setTelefoneResponsavel(entity.getTelefoneResponsavel());
        dto.setEmailResponsavel(entity.getEmailResponsavel());
        dto.setStatusEntrevistaFamiliar(entity.getStatusEntrevistaFamiliar());
        dto.setObservacoesEntrevistaFamiliar(entity.getObservacoesEntrevistaFamiliar());
        dto.setDataEntrevistaFamiliar(entity.getDataEntrevistaFamiliar());
        dto.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);
        
        if (entity.getHospital() != null) {
            dto.setHospitalId(entity.getHospital().getId());
            dto.setHospitalNome(entity.getHospital().getNome());
        }
        
        if (entity.getProtocolosME() != null) {
            dto.setProtocolosME(
                entity.getProtocolosME().stream()
                    .map(ProtocoloMEDTO::fromEntity)
                    .collect(Collectors.toList())
            );
        }
        
        return dto;
    }
}
