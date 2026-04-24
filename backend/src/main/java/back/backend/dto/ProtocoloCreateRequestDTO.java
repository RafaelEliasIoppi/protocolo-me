package back.backend.dto;

import javax.validation.constraints.NotNull;

public class ProtocoloCreateRequestDTO {

    @NotNull(message = "Paciente é obrigatório")
    private Long pacienteId;

    private String diagnosticoBasico;
    private String numeroProtocolo;

    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }

    public String getDiagnosticoBasico() { return diagnosticoBasico; }
    public void setDiagnosticoBasico(String diagnosticoBasico) { this.diagnosticoBasico = diagnosticoBasico; }

    public String getNumeroProtocolo() { return numeroProtocolo; }
    public void setNumeroProtocolo(String numeroProtocolo) { this.numeroProtocolo = numeroProtocolo; }
}