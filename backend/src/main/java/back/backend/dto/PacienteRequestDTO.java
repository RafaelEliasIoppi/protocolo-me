package back.backend.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PacienteRequestDTO {

    @NotBlank(message = "Nome obrigatório")
    private String nome;

    @NotBlank(message = "CPF obrigatório")
    private String cpf;

    @NotNull(message = "Data de nascimento obrigatória")
    private LocalDate dataNascimento;

    @NotBlank(message = "Gênero obrigatório")
    private String genero;

    @NotNull(message = "Hospital obrigatório")
    private Long hospitalId;

    private String hospitalOrigem;
    private String leito;
    private LocalDate dataInternacao;
    private String diagnosticoPrincipal;
    private String historicoMedico;
    private String nomeResponsavel;
    private String telefoneResponsavel;
    private String emailResponsavel;
    private String status;
    private String statusEntrevistaFamiliar;
    private String observacoesEntrevistaFamiliar;
    private LocalDateTime dataEntrevistaFamiliar;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public Long getHospitalId() { return hospitalId; }
    public void setHospitalId(Long hospitalId) { this.hospitalId = hospitalId; }

    public String getHospitalOrigem() { return hospitalOrigem; }
    public void setHospitalOrigem(String hospitalOrigem) { this.hospitalOrigem = hospitalOrigem; }

    public String getLeito() { return leito; }
    public void setLeito(String leito) { this.leito = leito; }

    public LocalDate getDataInternacao() { return dataInternacao; }
    public void setDataInternacao(LocalDate dataInternacao) { this.dataInternacao = dataInternacao; }

    public String getDiagnosticoPrincipal() { return diagnosticoPrincipal; }
    public void setDiagnosticoPrincipal(String diagnosticoPrincipal) { this.diagnosticoPrincipal = diagnosticoPrincipal; }

    public String getHistoricoMedico() { return historicoMedico; }
    public void setHistoricoMedico(String historicoMedico) { this.historicoMedico = historicoMedico; }

    public String getNomeResponsavel() { return nomeResponsavel; }
    public void setNomeResponsavel(String nomeResponsavel) { this.nomeResponsavel = nomeResponsavel; }

    public String getTelefoneResponsavel() { return telefoneResponsavel; }
    public void setTelefoneResponsavel(String telefoneResponsavel) { this.telefoneResponsavel = telefoneResponsavel; }

    public String getEmailResponsavel() { return emailResponsavel; }
    public void setEmailResponsavel(String emailResponsavel) { this.emailResponsavel = emailResponsavel; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getStatusEntrevistaFamiliar() { return statusEntrevistaFamiliar; }
    public void setStatusEntrevistaFamiliar(String statusEntrevistaFamiliar) { this.statusEntrevistaFamiliar = statusEntrevistaFamiliar; }

    public String getObservacoesEntrevistaFamiliar() { return observacoesEntrevistaFamiliar; }
    public void setObservacoesEntrevistaFamiliar(String observacoesEntrevistaFamiliar) { this.observacoesEntrevistaFamiliar = observacoesEntrevistaFamiliar; }

    public LocalDateTime getDataEntrevistaFamiliar() { return dataEntrevistaFamiliar; }
    public void setDataEntrevistaFamiliar(LocalDateTime dataEntrevistaFamiliar) { this.dataEntrevistaFamiliar = dataEntrevistaFamiliar; }
}