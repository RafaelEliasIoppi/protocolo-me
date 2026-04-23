package back.backend.dto;

import back.backend.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

    public static UsuarioDTO fromEntity(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        Long hospitalId = null;
        String hospitalNome = null;
        if (usuario.getHospital() != null) {
            hospitalId = usuario.getHospital().getId();
            hospitalNome = usuario.getHospital().getNome();
        }

        Long centralId = null;
        String centralNome = null;
        if (usuario.getCentralTransplantes() != null) {
            centralId = usuario.getCentralTransplantes().getId();
            centralNome = usuario.getCentralTransplantes().getNome();
        }

        return new UsuarioDTO(
            usuario.getId(),
            usuario.getEmail(),
            usuario.getNome(),
            usuario.getRole() != null ? usuario.getRole().name() : null,
            usuario.getAtivo(),
            usuario.getCrm(),
            usuario.getCoren(),
            usuario.getDataCriacao(),
            usuario.getDataAtualizacao(),
            hospitalId,
            hospitalNome,
            centralId,
            centralNome
        );
    }
}