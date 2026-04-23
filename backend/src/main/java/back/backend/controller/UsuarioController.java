package back.backend.controller;

import back.backend.model.Usuario;
import back.backend.model.Role;
import back.backend.service.UsuarioService;
import back.backend.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder; // ✅ IMPORT CORRETO
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
                        .body(Map.of("erro", "Cadastro público permite apenas MÉDICO ou ENFERMEIRO"));
            }

            Usuario salvo = usuarioService.registrar(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(toUsuarioResponse(salvo));

        } catch (RuntimeException e) {
            log.warn("Erro ao registrar usuário: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    // =========================
    // REGISTRO ADMIN
    // =========================
    @PostMapping("/admin/registrar")
    public ResponseEntity<?> registrarAdministrador(@RequestBody Usuario usuario) {
        try {
            if (usuario.getRole() == null) {
                return ResponseEntity.badRequest().body(Map.of("erro", "Informe a função"));
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
                        .body(Map.of("erro", "Primeiro usuário deve ser ADMIN"));
            }

            if (totalAdmins > 0 && !isAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("erro", "Apenas ADMIN pode cadastrar usuários"));
            }

            Usuario salvo = usuarioService.registrar(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(toUsuarioResponse(salvo));

        } catch (RuntimeException e) {
            log.warn("Erro ao registrar admin: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    // =========================
    // LOGIN
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
                        .body(Map.of("erro", "Email e senha são obrigatórios"));
            }

            Optional<Usuario> usuarioOpt = usuarioService.findByEmail(email);

            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("erro", "Credenciais inválidas"));
            }

            Usuario usuario = usuarioOpt.get();

            if (!Boolean.TRUE.equals(usuario.getAtivo())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("erro", "Usuário inativo"));
            }

            if (!passwordEncoder.matches(senha, usuario.getSenha())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("erro", "Credenciais inválidas"));
            }

            String token = jwtUtil.gerarToken(usuario.getEmail(), usuario.getRole().name());
            long expiraEm = jwtUtil.extractExpiration(token).getTime();

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("tokenExpiraEm", expiraEm);
            response.put("usuario", toUsuarioResponse(usuario));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erro interno no login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro interno no servidor"));
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
                        .body(Map.of("erro", "Usuário não autenticado"));
            }

            Usuario usuario = usuarioService.alterarMinhaSenha(
                    auth.getName(),
                    payload.get("senhaAtual"),
                    payload.get("senhaNova"),
                    payload.get("confirmarSenha")
            );

            return ResponseEntity.ok(Map.of(
                    "id", usuario.getId(),
                    "mensagem", "Senha alterada com sucesso"
            ));

        } catch (RuntimeException e) {
            log.warn("Erro ao alterar senha: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    // =========================
    // LISTAR
    // =========================
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listarUsuarios() {
        return ResponseEntity.ok(
                usuarioService.listarTodos()
                        .stream()
                        .map(this::toUsuarioResponse)
                        .collect(Collectors.toList())
        );
    }

    // =========================
    // UTIL
    // =========================
    private void normalizarEmail(Usuario usuario) {
        if (usuario.getEmail() != null) {
            usuario.setEmail(usuario.getEmail().trim().toLowerCase(Locale.ROOT));
        }
    }

    private Map<String, Object> toUsuarioResponse(Usuario usuario) {
        Map<String, Object> item = new HashMap<>();
        item.put("id", usuario.getId());
        item.put("email", usuario.getEmail());
        item.put("nome", usuario.getNome());
        item.put("role", usuario.getRole().name());
        item.put("ativo", usuario.getAtivo());
        item.put("crm", usuario.getCrm());
        item.put("coren", usuario.getCoren());
        return item;
    }
}