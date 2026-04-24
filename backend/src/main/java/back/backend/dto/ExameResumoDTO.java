package back.backend.dto;

import back.backend.service.ExameMEService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExameResumoDTO {

    private int totalExames;
    private int examesRealizados;
    private int examesPendentes;

    private int examesClinicos;
    private int examesClinicosTotal;

    private int examesComplementares;
    private int examesComplementaresTotal;

    private int examesLaboratoriais;
    private int examesLaboratoriaisTotal;

    // =========================
    // SERVICE -> DTO
    // =========================
    public static ExameResumoDTO fromService(ExameMEService.ExameResumo resumo) {
        if (resumo == null) return null;

        return ExameResumoDTO.builder()
                .totalExames(resumo.getTotalExames())
                .examesRealizados(resumo.getExamesRealizados())
                .examesPendentes(resumo.getExamesPendentes())

                .examesClinicos(getClinicos(resumo))
                .examesClinicosTotal(resumo.getExamesClinicosTotal())

                .examesComplementares(resumo.getExamesComplementares())
                .examesComplementaresTotal(resumo.getExamesComplementaresTotal())

                .examesLaboratoriais(resumo.getExamesLaboratoriais())
                .examesLaboratoriaisTotal(resumo.getExamesLaboratoriaisTotal())
                .build();
    }

    // =========================
    // COMPATIBILIDADE (legacy)
    // =========================
    private static int getClinicos(ExameMEService.ExameResumo r) {
        try {
            return r.getExames_Clinicos(); // legado
        } catch (Exception e) {
            return 0;
        }
    }
}