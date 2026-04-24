package back.backend.dto;

public class AuthResponseDTO {

    private String token;
    private Long tokenExpiraEm;
    private UsuarioDTO usuario;

    public AuthResponseDTO() {
    }

    public AuthResponseDTO(String token, Long tokenExpiraEm, UsuarioDTO usuario) {
        this.token = token;
        this.tokenExpiraEm = tokenExpiraEm;
        this.usuario = usuario;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getTokenExpiraEm() {
        return tokenExpiraEm;
    }

    public void setTokenExpiraEm(Long tokenExpiraEm) {
        this.tokenExpiraEm = tokenExpiraEm;
    }

    public UsuarioDTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioDTO usuario) {
        this.usuario = usuario;
    }
}