package back.backend.dto;

public class ResetSenhaDTO {

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