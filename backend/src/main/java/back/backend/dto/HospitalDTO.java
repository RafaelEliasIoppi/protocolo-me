package back.backend.dto;

import back.backend.model.Hospital;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private String responsavelMedico;

    public static HospitalDTO fromEntity(Hospital entity) {
        if (entity == null) {
            return null;
        }

        return new HospitalDTO(
            entity.getId(),
            entity.getNome(),
            entity.getCnpj(),
            entity.getEndereco(),
            entity.getCidade(),
            entity.getEstado(),
            entity.getTelefone(),
            entity.getEmail(),
            entity.getStatus() != null ? entity.getStatus().name() : null,
            entity.getDataCriacao(),
            entity.getDataAtualizacao(),
            entity.getResponsavelMedico()
        );
    }
}