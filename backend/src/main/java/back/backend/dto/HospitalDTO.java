package back.backend.dto;

import back.backend.model.Hospital;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HospitalDTO {

    private Long id;
    private String nome;
    private String cnpj;
    private String endereco;
    private String cidade;
    private String estado;
    private String telefone;
    private String email;
    private String status;
    private String responsavelMedico;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    // =========================
    // ENTITY -> DTO
    // =========================
    public static HospitalDTO fromEntity(Hospital entity) {

        if (entity == null) return null;

        return HospitalDTO.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .cnpj(entity.getCnpj())
                .endereco(entity.getEndereco())
                .cidade(entity.getCidade())
                .estado(entity.getEstado())
                .telefone(entity.getTelefone())
                .email(entity.getEmail())
                .status(entity.getStatus() != null ? entity.getStatus().name() : null)
                .responsavelMedico(entity.getResponsavelMedico())
                .dataCriacao(entity.getDataCriacao())
                .dataAtualizacao(entity.getDataAtualizacao())
                .build();
    }

    // =========================
    // DTO -> ENTITY (opcional mas SENIOR)
    // =========================
    public static Hospital toEntity(HospitalDTO dto) {

        if (dto == null) return null;

        Hospital hospital = new Hospital();

        hospital.setId(dto.getId());
        hospital.setNome(dto.getNome());
        hospital.setCnpj(dto.getCnpj());
        hospital.setEndereco(dto.getEndereco());
        hospital.setCidade(dto.getCidade());
        hospital.setEstado(dto.getEstado());
        hospital.setTelefone(dto.getTelefone());
        hospital.setEmail(dto.getEmail());
        hospital.setResponsavelMedico(dto.getResponsavelMedico());

        if (dto.getStatus() != null) {
            try {
                hospital.setStatus(Hospital.StatusHospital.valueOf(dto.getStatus()));
            } catch (Exception ignored) {
                // deixa null ou trata em camada superior
            }
        }

        return hospital;
    }
}