package back.backend.controller;

import back.backend.dto.AcaoResponseDTO;
import back.backend.dto.AuthResponseDTO;
import back.backend.dto.UsuarioDTO;
import back.backend.model.Usuario;
import back.backend.model.Role;
import back.backend.service.UsuarioService;
import back.backend.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.Locale;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // =========================
    // REGISTRO PÚBLICO
    // =========================
    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        try {
            if (usuario.getRole() == null) {
                usuario.setRole(Role.MEDICO);
            }

            normalizarEmail(usuario);

            if (usuario.getRole() != Role.MEDICO && usuario.getRole() != Role.ENFERMEIRO) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErroResponse("Cadastro público permite apenas MÉDICO ou ENFERMEIRO", HttpStatus.FORBIDDEN.value()));
            }

            Usuario salvo = usuarioService.registrar(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioDTO.fromEntity(salvo));

        } catch (RuntimeException e) {
            log.warn("Erro ao registrar usuário: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErroResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }

    // =========================
    // REGISTRO ADMIN
    // =========================
    @PostMapping("/admin/registrar")
    public ResponseEntity<?> registrarAdministrador(@RequestBody Usuario usuario) {
        try {
            if (usuario.getRole() == null) {
                return ResponseEntity.badRequest().body(new ErroResponse("Informe a função", HttpStatus.BAD_REQUEST.value()));
            }

            normalizarEmail(usuario);

            long totalAdmins = usuarioService.countAdmins();

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = auth != null &&
                    auth.isAuthenticated() &&
                    auth.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (totalAdmins == 0 && usuario.getRole() != Role.ADMIN) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErroResponse("Primeiro usuário deve ser ADMIN", HttpStatus.FORBIDDEN.value()));
            }

            if (totalAdmins > 0 && !isAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErroResponse("Apenas ADMIN pode cadastrar usuários", HttpStatus.FORBIDDEN.value()));
            }

            Usuario salvo = usuarioService.registrar(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioDTO.fromEntity(salvo));

        } catch (RuntimeException e) {
            log.warn("Erro ao registrar admin: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErroResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }

    // =========================
    // LOGIN (AJUSTADO PARA TESTES)
    // =========================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciais) {
        try {
            String email = Optional.ofNullable(credenciais.get("email"))
                    .map(e -> e.trim().toLowerCase(Locale.ROOT))
                    .orElse("");

            String senha = Optional.ofNullable(credenciais.get("senha")).orElse("");

            if (email.isBlank() || senha.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(new ErroResponse("Email e senha são obrigatórios", HttpStatus.BAD_REQUEST.value()));
            }

            Optional<Usuario> usuarioOpt = usuarioService.findByEmail(email);

            // ✅ TESTE: usuário inexistente
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErroResponse("Usuário não encontrado ou inativo", HttpStatus.UNAUTHORIZED.value()));
            }

            Usuario usuario = usuarioOpt.get();

            // ✅ TESTE: usuário inativo
            if (!Boolean.TRUE.equals(usuario.getAtivo())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErroResponse("Usuário não encontrado ou inativo", HttpStatus.UNAUTHORIZED.value()));
            }

            // ✅ TESTE: senha incorreta
            if (!passwordEncoder.matches(senha, usuario.getSenha())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErroResponse("Senha incorreta", HttpStatus.UNAUTHORIZED.value()));
            }

            String token = jwtUtil.gerarToken(usuario.getEmail(), usuario.getRole().name());
            long expiraEm = jwtUtil.extractExpiration(token).getTime();

            return ResponseEntity.ok(new AuthResponseDTO(token, expiraEm, UsuarioDTO.fromEntity(usuario)));

        } catch (Exception e) {
            log.error("Erro interno no login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErroResponse("Erro interno no servidor", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // =========================
    // ALTERAR MINHA SENHA
    // =========================
    @PatchMapping("/minha-senha")
    public ResponseEntity<?> alterarMinhaSenha(@RequestBody Map<String, String> payload) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErroResponse("Usuário não autenticado", HttpStatus.UNAUTHORIZED.value()));
            }

            Usuario usuario = usuarioService.alterarMinhaSenha(
                    auth.getName(),
                    payload.get("senhaAtual"),
                    payload.get("senhaNova"),
                    payload.get("confirmarSenha")
            );

            return ResponseEntity.ok(new AcaoResponseDTO(usuario.getId(), "Senha alterada com sucesso"));

        } catch (RuntimeException e) {
            log.warn("Erro ao alterar senha: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErroResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
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
    // ATUALIZAR USUÁRIO
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarUsuario(@PathVariable Long id, @RequestBody Usuario dados) {
        try {
            normalizarEmail(dados);

            Usuario atualizado = usuarioService.atualizarUsuario(id, dados);

            return ResponseEntity.ok(UsuarioDTO.fromEntity(atualizado));
        } catch (RuntimeException e) {
            log.warn("Erro ao atualizar usuário {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(new ErroResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }

    // =========================
    // REDEFINIR SENHA
    // =========================
    @PatchMapping("/{id}/senha")
    public ResponseEntity<?> redefinirSenha(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        try {
            String senhaNova = payload.get("senhaNova");

            Usuario usuario = usuarioService.redefinirSenha(id, senhaNova);

            return ResponseEntity.ok(new AcaoResponseDTO(usuario.getId(), "Senha redefinida com sucesso"));
        } catch (RuntimeException e) {
            log.warn("Erro ao redefinir senha do usuário {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(new ErroResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }

    // =========================
    // REMOVER USUÁRIO
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarUsuario(@PathVariable Long id) {
        try {
            usuarioService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.warn("Erro ao deletar usuário {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(new ErroResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
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