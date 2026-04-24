package back.backend.dto;

import javax.validation.constraints.NotBlank;

public class ResetSenhaDTO {

    @NotBlank(message = "Senha nova obrigatória")
    private String senhaNova;

    public ResetSenhaDTO() {
    }

    public ResetSenhaDTO(String senhaNova) {
        this.senhaNova = senhaNova;
    }

    public String getSenhaNova() {
        return senhaNova;
    }

    public void setSenhaNova(String senhaNova) {
        this.senhaNova = senhaNova;
    }
}