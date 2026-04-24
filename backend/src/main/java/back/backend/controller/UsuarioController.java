package back.backend.controller;

import back.backend.dto.*;
import back.backend.model.Usuario;
import back.backend.model.Role;
import back.backend.service.UsuarioService;
import back.backend.security.JwtUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    // =========================
    // REGISTRO PÚBLICO
    // =========================
    @PostMapping
    public ResponseEntity<UsuarioDTO> registrar(@RequestBody Usuario usuario) {

        if (usuario.getRole() == null) {
            usuario.setRole(Role.MEDICO);
        }

        normalizarEmail(usuario);

        if (usuario.getRole() != Role.MEDICO && usuario.getRole() != Role.ENFERMEIRO) {
            throw new RuntimeException("Cadastro público permite apenas MÉDICO ou ENFERMEIRO");
        }

        Usuario salvo = usuarioService.registrar(usuario);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UsuarioDTO.fromEntity(salvo));
    }

    // =========================
    // REGISTRO ADMIN
    // =========================
    @PostMapping("/admin/registrar")
    public ResponseEntity<UsuarioDTO> registrarAdmin(@RequestBody Usuario usuario) {

        if (usuario.getRole() == null) {
            throw new RuntimeException("Informe a função");
        }

        normalizarEmail(usuario);

        usuarioService.validarPermissaoCriacaoAdmin(usuario);

        Usuario salvo = usuarioService.registrar(usuario);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UsuarioDTO.fromEntity(salvo));
    }

    // =========================
    // LOGIN
    // =========================
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request) {

        Usuario usuario = usuarioService.autenticar(request.getEmail(), request.getSenha());

        String token = jwtUtil.gerarToken(usuario.getEmail(), usuario.getRole().name());
        long expiraEm = jwtUtil.extractExpiration(token).getTime();

        return ResponseEntity.ok(
                new AuthResponseDTO(token, expiraEm, UsuarioDTO.fromEntity(usuario))
        );
    }

    // =========================
    // MINHA SENHA
    // =========================
    @PatchMapping("/minha-senha")
    public ResponseEntity<AcaoResponseDTO> alterarMinhaSenha(@RequestBody AlterarSenhaDTO dto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Usuario usuario = usuarioService.alterarMinhaSenha(
                auth.getName(),
                dto.getSenhaAtual(),
                dto.getSenhaNova(),
                dto.getConfirmarSenha()
        );

        return ResponseEntity.ok(new AcaoResponseDTO(usuario.getId(), "Senha alterada com sucesso"));
    }

    // =========================
    // LISTAR
    // =========================
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        return ResponseEntity.ok(
                usuarioService.listarTodos()
                        .stream()
                        .map(UsuarioDTO::fromEntity)
                        .collect(Collectors.toList())
        );
    }

    // =========================
    // ATUALIZAR
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> atualizarUsuario(@PathVariable Long id, @RequestBody Usuario dados) {

        normalizarEmail(dados);

        Usuario atualizado = usuarioService.atualizarUsuario(id, dados);

        return ResponseEntity.ok(UsuarioDTO.fromEntity(atualizado));
    }

    // =========================
    // RESET SENHA
    // =========================
    @PatchMapping("/{id}/senha")
    public ResponseEntity<AcaoResponseDTO> redefinirSenha(@PathVariable Long id,
                                                         @RequestBody ResetSenhaDTO dto) {

        Usuario usuario = usuarioService.redefinirSenha(id, dto.getSenhaNova());

        return ResponseEntity.ok(new AcaoResponseDTO(usuario.getId(), "Senha redefinida"));
    }

    // =========================
    // DELETE
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // =========================
    // UTIL
    // =========================
    private void normalizarEmail(Usuario usuario) {
        if (usuario.getEmail() != null) {
            usuario.setEmail(usuario.getEmail().trim().toLowerCase(Locale.ROOT));
        }
    }
}