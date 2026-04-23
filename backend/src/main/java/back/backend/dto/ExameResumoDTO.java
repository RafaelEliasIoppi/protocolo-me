package back.backend.dto;

import back.backend.service.ExameMEService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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

    public static ExameResumoDTO fromService(ExameMEService.ExameResumo resumo) {
        if (resumo == null) {
            return null;
        }

        return new ExameResumoDTO(
            resumo.getTotalExames(),
            resumo.getExamesRealizados(),
            resumo.getExamesPendentes(),
            resumo.getExames_Clinicos(),
            resumo.getExamesClinicosTotal(),
            resumo.getExamesComplementares(),
            resumo.getExamesComplementaresTotal(),
            resumo.getExamesLaboratoriais(),
            resumo.getExamesLaboratoriaisTotal()
        );
    }
}