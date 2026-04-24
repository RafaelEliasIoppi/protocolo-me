package back.backend.dto;

import back.backend.model.Hospital;

import java.time.LocalDateTime;

public class HospitalDTO {

    private Long id;
    private String nome;
    private String cnpj;
    private String endereco;
    private String cidade;
    private String estado;
    private String telefone;
    private String email;
    private String status;
    private String responsavelMedico;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public HospitalDTO() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getResponsavelMedico() { return responsavelMedico; }
    public void setResponsavelMedico(String responsavelMedico) { this.responsavelMedico = responsavelMedico; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }

    // =========================
    // ENTITY -> DTO
    // =========================
    public static HospitalDTO fromEntity(Hospital entity) {

        if (entity == null) return null;

        HospitalDTO dto = new HospitalDTO();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setCnpj(entity.getCnpj());
        dto.setEndereco(entity.getEndereco());
        dto.setCidade(entity.getCidade());
        dto.setEstado(entity.getEstado());
        dto.setTelefone(entity.getTelefone());
        dto.setEmail(entity.getEmail());
        dto.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);
        dto.setResponsavelMedico(entity.getResponsavelMedico());
        dto.setDataCriacao(entity.getDataCriacao());
        dto.setDataAtualizacao(entity.getDataAtualizacao());
        return dto;
    }

    // =========================
    // DTO -> ENTITY (opcional mas SENIOR)
    // =========================
    public static Hospital toEntity(HospitalDTO dto) {

        if (dto == null) return null;

        Hospital hospital = new Hospital();

        hospital.setId(dto.getId());
        hospital.setNome(dto.getNome());
        hospital.setCnpj(dto.getCnpj());
        hospital.setEndereco(dto.getEndereco());
        hospital.setCidade(dto.getCidade());
        hospital.setEstado(dto.getEstado());
        hospital.setTelefone(dto.getTelefone());
        hospital.setEmail(dto.getEmail());
        hospital.setResponsavelMedico(dto.getResponsavelMedico());

        if (dto.getStatus() != null) {
            try {
                hospital.setStatus(Hospital.StatusHospital.valueOf(dto.getStatus()));
            } catch (Exception ignored) {
                // deixa null ou trata em camada superior
            }
        }

        return hospital;
    }
}