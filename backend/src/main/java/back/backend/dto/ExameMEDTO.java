package back.backend.dto;

import java.time.LocalDateTime;

public class ExameMEDTO {

    private Long id;
    private Long protocoloId;
    private String protocoloNumero;
    private String categoria;
    private String tipoExame;
    private String descricao;
    private String resultado;
    private Boolean resultadoPositivo;
    private LocalDateTime dataRealizacao;
    private String responsavel;
    private String observacoes;

    // =========================
    // VALIDAÇÃO PELA CENTRAL
    // =========================
    private String statusValidacao;
    private String validadoPor;
    private LocalDateTime dataValidacao;
    private String observacoesValidacao;

    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public ExameMEDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProtocoloId() {
        return protocoloId;
    }

    public void setProtocoloId(Long protocoloId) {
        this.protocoloId = protocoloId;
    }

    public String getProtocoloNumero() {
        return protocoloNumero;
    }

    public void setProtocoloNumero(String protocoloNumero) {
        this.protocoloNumero = protocoloNumero;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTipoExame() {
        return tipoExame;
    }

    public void setTipoExame(String tipoExame) {
        this.tipoExame = tipoExame;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public Boolean getResultadoPositivo() {
        return resultadoPositivo;
    }

    public void setResultadoPositivo(Boolean resultadoPositivo) {
        this.resultadoPositivo = resultadoPositivo;
    }

    public LocalDateTime getDataRealizacao() {
        return dataRealizacao;
    }

    public void setDataRealizacao(LocalDateTime dataRealizacao) {
        this.dataRealizacao = dataRealizacao;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public String getStatusValidacao() {
        return statusValidacao;
    }

    public void setStatusValidacao(String statusValidacao) {
        this.statusValidacao = statusValidacao;
    }

    public String getValidadoPor() {
        return validadoPor;
    }

    public void setValidadoPor(String validadoPor) {
        this.validadoPor = validadoPor;
    }

    public LocalDateTime getDataValidacao() {
        return dataValidacao;
    }

    public void setDataValidacao(LocalDateTime dataValidacao) {
        this.dataValidacao = dataValidacao;
    }

    public String getObservacoesValidacao() {
        return observacoesValidacao;
    }

    public void setObservacoesValidacao(String observacoesValidacao) {
        this.observacoesValidacao = observacoesValidacao;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }
}
