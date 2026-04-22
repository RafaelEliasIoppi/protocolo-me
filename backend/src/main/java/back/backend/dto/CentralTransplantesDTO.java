package back.backend.dto;

import javax.validation.constraints.*;
import java.io.Serializable;

public class CentralTransplantesDTO implements Serializable {
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "CNPJ é obrigatório")
    private String cnpj;

    @NotBlank(message = "Endereço é obrigatório")
    private String endereco;

    @NotBlank(message = "Cidade é obrigatória")
    private String cidade;

    @NotBlank(message = "Estado é obrigatório")
    private String estado;

    @NotBlank(message = "Telefone é obrigatório")
    private String telefone;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Coordenador é obrigatório")
    private String coordenador;

    private String telefonePlantao;
    private String emailPlantao;
    private String telefoneCoordenador;
    private Integer capacidadeProcessamento;
    private String especialidadesOrgaos;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCoordenador() {
        return coordenador;
    }

    public void setCoordenador(String coordenador) {
        this.coordenador = coordenador;
    }

    public String getTelefonePlantao() {
        return telefonePlantao;
    }

    public void setTelefonePlantao(String telefonePlantao) {
        this.telefonePlantao = telefonePlantao;
    }

    public String getEmailPlantao() {
        return emailPlantao;
    }

    public void setEmailPlantao(String emailPlantao) {
        this.emailPlantao = emailPlantao;
    }

    public String getTelefoneCoordenador() {
        return telefoneCoordenador;
    }

    public void setTelefoneCoordenador(String telefoneCoordenador) {
        this.telefoneCoordenador = telefoneCoordenador;
    }

    public Integer getCapacidadeProcessamento() {
        return capacidadeProcessamento;
    }

    public void setCapacidadeProcessamento(Integer capacidadeProcessamento) {
        this.capacidadeProcessamento = capacidadeProcessamento;
    }

    public String getEspecialidadesOrgaos() {
        return especialidadesOrgaos;
    }

    public void setEspecialidadesOrgaos(String especialidadesOrgaos) {
        this.especialidadesOrgaos = especialidadesOrgaos;
    }
}
