package back.backend.dto;

public class AlterarSenhaDTO {

    private String senhaAtual;
    private String senhaNova;
    private String confirmarSenha;

    public AlterarSenhaDTO() {
    }

    public AlterarSenhaDTO(String senhaAtual, String senhaNova, String confirmarSenha) {
        this.senhaAtual = senhaAtual;
        this.senhaNova = senhaNova;
        this.confirmarSenha = confirmarSenha;
    }

    public String getSenhaAtual() {
        return senhaAtual;
    }

    public void setSenhaAtual(String senhaAtual) {
        this.senhaAtual = senhaAtual;
    }

    public String getSenhaNova() {
        return senhaNova;
    }

    public void setSenhaNova(String senhaNova) {
        this.senhaNova = senhaNova;
    }

    public String getConfirmarSenha() {
        return confirmarSenha;
    }

    public void setConfirmarSenha(String confirmarSenha) {
        this.confirmarSenha = confirmarSenha;
    }
}