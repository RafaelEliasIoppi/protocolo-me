package back.backend.dto;

import back.backend.model.ProtocoloME;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProtocoloMEDTO {

    private Long id;
    private String numeroProtocolo;
    private String hospitalOrigem;
    private String medicoResponsavel;
    private String enfermeiro;
    private String status;
    private String diagnosticoBasico;
    private String causaMorte;
    private String observacoes;
    private Boolean testeClinico1Realizado;
    private LocalDateTime dataTesteClinico1;
    private Boolean testeClinico2Realizado;
    private LocalDateTime dataTesteClinico2;
    private Boolean testesComplementaresRealizados;
    private String testesComplementares;
    private LocalDateTime dataTesteComplementar;
    private Boolean familiaNotificada;
    private LocalDateTime dataNotificacaoFamilia;
    private Boolean autopsiaAutorizada;
    private String orgaosDisponiveis;
    private Boolean preservacaoOrgaos;
    private LocalDateTime dataPreservacao;
    private LocalDateTime dataNotificacao;
    private LocalDateTime dataConfirmacaoME;
    private LocalDateTime dataSaidaHospital;
    private String relatorioFinalEditavel;
    private String relatorioFinalAtualizadoPor;
    private LocalDateTime relatorioFinalAtualizadoEm;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private Long centralTransplantesId;
    private String centralTransplantesNome;
    private List<OrgaoDoadoDTO> orgaosDoados;

    public static ProtocoloMEDTO fromEntity(ProtocoloME entity) {
        if (entity == null) return null;
        ProtocoloMEDTO dto = new ProtocoloMEDTO();
        dto.setId(entity.getId());
        dto.setNumeroProtocolo(entity.getNumeroProtocolo());
        dto.setHospitalOrigem(entity.getHospitalOrigem());
        dto.setMedicoResponsavel(entity.getMedicoResponsavel());
        dto.setEnfermeiro(entity.getEnfermeiro());
        dto.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);
        dto.setDiagnosticoBasico(entity.getDiagnosticoBasico());
        dto.setCausaMorte(entity.getCausaMorte());
        dto.setObservacoes(entity.getObservacoes());
        dto.setTesteClinico1Realizado(entity.getTesteClinico1Realizado());
        dto.setDataTesteClinico1(entity.getDataTesteClinico1());
        dto.setTesteClinico2Realizado(entity.getTesteClinico2Realizado());
        dto.setDataTesteClinico2(entity.getDataTesteClinico2());
        dto.setTestesComplementaresRealizados(entity.getTestesComplementaresRealizados());
        dto.setTestesComplementares(entity.getTestesComplementares());
        dto.setDataTesteComplementar(entity.getDataTesteComplementar());
        dto.setFamiliaNotificada(entity.getFamiliaNotificada());
        dto.setDataNotificacaoFamilia(entity.getDataNotificacaoFamilia());
        dto.setAutopsiaAutorizada(entity.getAutopsiaAutorizada());
        dto.setOrgaosDisponiveis(entity.getOrgaosDisponiveis());
        dto.setPreservacaoOrgaos(entity.getPreservacaoOrgaos());
        dto.setDataPreservacao(entity.getDataPreservacao());
        dto.setDataNotificacao(entity.getDataNotificacao());
        dto.setDataConfirmacaoME(entity.getDataConfirmacaoME());
        dto.setDataSaidaHospital(entity.getDataSaidaHospital());
        dto.setRelatorioFinalEditavel(entity.getRelatorioFinalEditavel());
        dto.setRelatorioFinalAtualizadoPor(entity.getRelatorioFinalAtualizadoPor());
        dto.setRelatorioFinalAtualizadoEm(entity.getRelatorioFinalAtualizadoEm());
        dto.setDataCriacao(entity.getDataCriacao());
        dto.setDataAtualizacao(entity.getDataAtualizacao());
        
        if (entity.getCentralTransplantes() != null) {
            dto.setCentralTransplantesId(entity.getCentralTransplantes().getId());
            dto.setCentralTransplantesNome(entity.getCentralTransplantes().getNome());
        }
        
        if (entity.getOrgaosDoados() != null && Hibernate.isInitialized(entity.getOrgaosDoados())) {
            dto.setOrgaosDoados(
                entity.getOrgaosDoados().stream()
                    .map(OrgaoDoadoDTO::fromEntity)
                    .collect(Collectors.toList())
            );
        }
        
        return dto;
    }
}
