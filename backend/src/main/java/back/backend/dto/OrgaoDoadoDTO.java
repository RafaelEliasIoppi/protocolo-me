package back.backend.dto;

import back.backend.model.OrgaoDoado;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrgaoDoadoDTO {

    private Long id;

    private Long protocoloId;
    private String numeroProtocolo;

    private String nomeOrgao;
    private String status;

    private String motivo;
    private String hospitalReceptor;
    private String pacienteReceptor;
    private String cpfReceptor;

    private LocalDateTime dataArmazenamento;
    private LocalDateTime dataImplantacao;
    private LocalDateTime dataDescarte;

    private String motivoDescarte;
    private String observacoes;

    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public static OrgaoDoadoDTO fromEntity(OrgaoDoado entity) {
        if (entity == null) return null;

        Long protocoloId = null;
        String numeroProtocolo = null;

        if (entity.getProtocoloME() != null) {
            protocoloId = entity.getProtocoloME().getId();
            numeroProtocolo = entity.getProtocoloME().getNumeroProtocolo();
        }

        return new OrgaoDoadoDTO(
                entity.getId(),
                protocoloId,
                numeroProtocolo,
                entity.getNomeOrgao(),
                entity.getStatus() != null ? entity.getStatus().name() : null,
                entity.getMotivo(),
                entity.getHospitalReceptor(),
                entity.getPacienteReceptor(),
                entity.getCpfReceptor(),
                entity.getDataArmazenamento(),
                entity.getDataImplantacao(),
                entity.getDataDescarte(),
                entity.getMotivoDescarte(),
                entity.getObservacoes(),
                entity.getDataCriacao(),
                entity.getDataAtualizacao()
        );
    }
}