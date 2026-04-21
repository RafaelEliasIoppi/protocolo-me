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

    // Getters e setters
    // ...
}
