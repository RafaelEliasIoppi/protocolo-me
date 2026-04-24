package back.backend.dto;

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

    public ExameResumoDTO() {
    }

    public int getTotalExames() { return totalExames; }
    public void setTotalExames(int totalExames) { this.totalExames = totalExames; }

    public int getExamesRealizados() { return examesRealizados; }
    public void setExamesRealizados(int examesRealizados) { this.examesRealizados = examesRealizados; }

    public int getExamesPendentes() { return examesPendentes; }
    public void setExamesPendentes(int examesPendentes) { this.examesPendentes = examesPendentes; }

    public int getExamesClinicos() { return examesClinicos; }
    public void setExamesClinicos(int examesClinicos) { this.examesClinicos = examesClinicos; }

    public int getExamesClinicosTotal() { return examesClinicosTotal; }
    public void setExamesClinicosTotal(int examesClinicosTotal) { this.examesClinicosTotal = examesClinicosTotal; }

    public int getExamesComplementares() { return examesComplementares; }
    public void setExamesComplementares(int examesComplementares) { this.examesComplementares = examesComplementares; }

    public int getExamesComplementaresTotal() { return examesComplementaresTotal; }
    public void setExamesComplementaresTotal(int examesComplementaresTotal) { this.examesComplementaresTotal = examesComplementaresTotal; }

    public int getExamesLaboratoriais() { return examesLaboratoriais; }
    public void setExamesLaboratoriais(int examesLaboratoriais) { this.examesLaboratoriais = examesLaboratoriais; }

    public int getExamesLaboratoriaisTotal() { return examesLaboratoriaisTotal; }
    public void setExamesLaboratoriaisTotal(int examesLaboratoriaisTotal) { this.examesLaboratoriaisTotal = examesLaboratoriaisTotal; }
}