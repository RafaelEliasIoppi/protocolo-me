package back.backend.dto;

import back.backend.model.Paciente;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PacienteDTO {

    private Long id;
    private String nome;
    private String cpf;
    private LocalDate dataNascimento;
    private String genero;

    private Long hospitalId;
    private String hospitalNome;
    private String hospitalOrigem;

    private String leito;
    private String diagnosticoPrincipal;
    private String historicoMedico;

    private String nomeResponsavel;
    private String telefoneResponsavel;
    private String emailResponsavel;

    private String status;
    private String statusEntrevistaFamiliar;
    private String observacoesEntrevistaFamiliar;
    private LocalDateTime dataEntrevistaFamiliar;

    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public static PacienteDTO fromEntity(Paciente entity) {
        if (entity == null) return null;

        Long hospitalId = null;
        String hospitalNome = null;

        if (entity.getHospital() != null) {
            hospitalId = entity.getHospital().getId();
            hospitalNome = entity.getHospital().getNome();
        }

        return new PacienteDTO(
                entity.getId(),
                entity.getNome(),
                entity.getCpf(),
                entity.getDataNascimento(),
                entity.getGenero() != null ? entity.getGenero().name() : null,

                hospitalId,
                hospitalNome,
                entity.getHospitalOrigem(),

                entity.getLeito(),
                entity.getDiagnosticoPrincipal(),
                entity.getHistoricoMedico(),

                entity.getNomeResponsavel(),
                entity.getTelefoneResponsavel(),
                entity.getEmailResponsavel(),

                entity.getStatus() != null ? entity.getStatus().name() : null,
                entity.getStatusEntrevistaFamiliar() != null ? entity.getStatusEntrevistaFamiliar().name() : null,
                entity.getObservacoesEntrevistaFamiliar(),
                entity.getDataEntrevistaFamiliar(),

                entity.getDataCriacao(),
                entity.getDataAtualizacao()
        );
    }
}