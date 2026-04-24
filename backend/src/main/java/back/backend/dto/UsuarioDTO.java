package back.backend.dto;

import back.backend.model.Usuario;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
        if (usuario == null) return null;

        return UsuarioDTO.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .nome(usuario.getNome())
                .role(usuario.getRole() != null ? usuario.getRole().name() : null)
                .ativo(usuario.getAtivo())
                .crm(usuario.getCrm())
                .coren(usuario.getCoren())
                .dataCriacao(usuario.getDataCriacao())
                .dataAtualizacao(usuario.getDataAtualizacao())

                .hospitalId(usuario.getHospital() != null ? usuario.getHospital().getId() : null)
                .hospitalNome(usuario.getHospital() != null ? usuario.getHospital().getNome() : null)

                .centralTransplantesId(usuario.getCentralTransplantes() != null ? usuario.getCentralTransplantes().getId() : null)
                .centralTransplantesNome(usuario.getCentralTransplantes() != null ? usuario.getCentralTransplantes().getNome() : null)

                .build();
    }
}