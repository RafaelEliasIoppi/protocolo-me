package back.backend.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class ExameRequestDTO {

    @NotNull(message = "Protocolo é obrigatório")
    private Long protocoloId;

    @NotNull(message = "Categoria é obrigatória")
    private String categoria;

    @NotNull(message = "Tipo de exame é obrigatório")
    private String tipoExame;

    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;

    private String resultado;
    private Boolean resultadoPositivo;
    private LocalDateTime dataRealizacao;
    private String responsavel;
    private String observacoes;

    public Long getProtocoloId() { return protocoloId; }
    public void setProtocoloId(Long protocoloId) { this.protocoloId = protocoloId; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getTipoExame() { return tipoExame; }
    public void setTipoExame(String tipoExame) { this.tipoExame = tipoExame; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }

    public Boolean getResultadoPositivo() { return resultadoPositivo; }
    public void setResultadoPositivo(Boolean resultadoPositivo) { this.resultadoPositivo = resultadoPositivo; }

    public LocalDateTime getDataRealizacao() { return dataRealizacao; }
    public void setDataRealizacao(LocalDateTime dataRealizacao) { this.dataRealizacao = dataRealizacao; }

    public String getResponsavel() { return responsavel; }
    public void setResponsavel(String responsavel) { this.responsavel = responsavel; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}