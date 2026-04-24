package back.backend.dto;

import java.time.LocalDateTime;

public class UsuarioDTO {

    private Long id;
    private String email;
    private String nome;
    private String role;
    private Boolean ativo;
    private String crm;
    private String coren;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    private Long hospitalId;
    private String hospitalNome;

    private Long centralTransplantesId;
    private String centralTransplantesNome;

    public UsuarioDTO() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public String getCrm() { return crm; }
    public void setCrm(String crm) { this.crm = crm; }

    public String getCoren() { return coren; }
    public void setCoren(String coren) { this.coren = coren; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }

    public Long getHospitalId() { return hospitalId; }
    public void setHospitalId(Long hospitalId) { this.hospitalId = hospitalId; }

    public String getHospitalNome() { return hospitalNome; }
    public void setHospitalNome(String hospitalNome) { this.hospitalNome = hospitalNome; }

    public Long getCentralTransplantesId() { return centralTransplantesId; }
    public void setCentralTransplantesId(Long centralTransplantesId) { this.centralTransplantesId = centralTransplantesId; }

    public String getCentralTransplantesNome() { return centralTransplantesNome; }
    public void setCentralTransplantesNome(String centralTransplantesNome) { this.centralTransplantesNome = centralTransplantesNome; }
}