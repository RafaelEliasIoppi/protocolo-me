package back.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class UsuarioRequestDTO {

    @NotBlank(message = "Email obrigatório")
    private String email;

    private String senha;

    @NotBlank(message = "Nome obrigatório")
    private String nome;

    private String crm;
    private String coren;
    private String role;
    private Long hospitalId;
    private Long centralTransplantesId;
    private Boolean ativo;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCrm() { return crm; }
    public void setCrm(String crm) { this.crm = crm; }

    public String getCoren() { return coren; }
    public void setCoren(String coren) { this.coren = coren; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Long getHospitalId() { return hospitalId; }
    public void setHospitalId(Long hospitalId) { this.hospitalId = hospitalId; }

    public Long getCentralTransplantesId() { return centralTransplantesId; }
    public void setCentralTransplantesId(Long centralTransplantesId) { this.centralTransplantesId = centralTransplantesId; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}