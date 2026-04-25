package back.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class ProtocoloRelatorioRequestDTO {

    @NotBlank(message = "Texto do relatório é obrigatório")
    private String textoRelatorio;

    @NotBlank(message = "Usuário responsável é obrigatório")
    private String atualizadoPor;

    public String getTextoRelatorio() { return textoRelatorio; }
    public void setTextoRelatorio(String textoRelatorio) { this.textoRelatorio = textoRelatorio; }

    public String getAtualizadoPor() { return atualizadoPor; }
    public void setAtualizadoPor(String atualizadoPor) { this.atualizadoPor = atualizadoPor; }
}