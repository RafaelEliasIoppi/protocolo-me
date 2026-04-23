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
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        try {
            if (usuario.getRole() == null) {
                usuario.setRole(Role.MEDICO);
            }

            if (usuario.getEmail() != null) {
                usuario.setEmail(usuario.getEmail().trim().toLowerCase(Locale.ROOT));
            }

            if (usuario.getRole() != Role.MEDICO && usuario.getRole() != Role.ENFERMEIRO) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("erro", "Cadastro público permite apenas perfis MÉDICO ou ENFERMEIRO"));
            }

            Usuario usuarioRegistrado = usuarioService.registrar(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(toUsuarioResponse(usuarioRegistrado));
        } catch (RuntimeException e) {
            log.warn("Erro ao registrar usuário: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", e.getMessage()));
        }
    }

    @PostMapping("/admin/registrar")
    public ResponseEntity<?> registrarAdministrador(@RequestBody Usuario usuario) {
        try {
            if (usuario.getRole() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", "Informe a função do usuário"));
            }

            if (usuario.getEmail() != null) {
                usuario.setEmail(usuario.getEmail().trim().toLowerCase(Locale.ROOT));
            }

            long totalAdmins = usuarioService.countAdmins();
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            boolean autenticadoComoAdmin = authentication != null
                && authentication.isAuthenticated()
                && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (totalAdmins == 0 && usuario.getRole() != Role.ADMIN) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("erro", "O primeiro usuário administrativo deve ser ADMIN"));
            } else if (totalAdmins > 0 && !autenticadoComoAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("erro", "Apenas administradores podem criar outros usuários"));
            }

            Usuario usuarioRegistrado = usuarioService.registrar(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(toUsuarioResponse(usuarioRegistrado));
        } catch (RuntimeException e) {
            log.warn("Erro ao registrar usuário administrativo: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listarUsuarios() {
        List<Map<String, Object>> usuarios = usuarioService.listarTodos().stream()
                .map(this::toUsuarioResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(usuarios);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarUsuarioAdmin(@PathVariable Long id, @RequestBody Usuario usuarioAtualizado) {
        try {
            Usuario usuarioSalvo = usuarioService.atualizarUsuario(id, usuarioAtualizado);
            return ResponseEntity.ok(toUsuarioResponse(usuarioSalvo));
        } catch (RuntimeException e) {
            log.warn("Erro ao atualizar usuário {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/senha")
    public ResponseEntity<?> redefinirSenha(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        try {
            String senhaNova = payload.get("senhaNova");
            Usuario usuarioSalvo = usuarioService.redefinirSenha(id, senhaNova);
            return ResponseEntity.ok(Map.of("id", usuarioSalvo.getId(), "mensagem", "Senha redefinida com sucesso"));
        } catch (RuntimeException e) {
            log.warn("Erro ao redefinir senha do usuário {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", e.getMessage()));
        }
    }

    @PatchMapping("/minha-senha")
    public ResponseEntity<?> alterarMinhaSenha(@RequestBody Map<String, String> payload) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("erro", "Usuário não autenticado"));
            }

            Usuario usuarioSalvo = usuarioService.alterarMinhaSenha(
                    authentication.getName(),
                    payload.get("senhaAtual"),
                    payload.get("senhaNova"),
                    payload.get("confirmarSenha")
            );

            return ResponseEntity.ok(Map.of("id", usuarioSalvo.getId(), "mensagem", "Senha alterada com sucesso"));
        } catch (RuntimeException e) {
            log.warn("Erro ao alterar senha do usuário autenticado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciais) {
        try {
            String email = credenciais.get("email");
            String senha = credenciais.get("senha");

            if (email != null) {
                email = email.trim().toLowerCase(Locale.ROOT);
            }

            if (email == null || email.isBlank() || senha == null || senha.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", "Email e senha são obrigatórios"));
            }

            Optional<Usuario> usuarioOpt = usuarioService.findByEmail(email);
            if (usuarioOpt.isEmpty() || !usuarioOpt.get().getAtivo()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("erro", "Usuário não encontrado ou inativo"));
            }

            Usuario usuario = usuarioOpt.get();
            if (!passwordEncoder.matches(senha, usuario.getSenha())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("erro", "Senha incorreta"));
            }

            String token = jwtUtil.gerarToken(usuario.getEmail(), usuario.getRole().name());
            long tokenExpiraEm = jwtUtil.extractExpiration(token).getTime();

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("tokenExpiraEm", tokenExpiraEm);
            response.put("usuario", toUsuarioResponse(usuario));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro interno no login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("erro", "Erro ao fazer login"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obterUsuario(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.findById(id);
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", "Usuário não encontrado"));
        }
        return ResponseEntity.ok(toUsuarioResponse(usuario.get()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarUsuario(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.findById(id);
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", "Usuário não encontrado"));
        }
        usuarioService.deletar(id);
        return ResponseEntity.ok(Map.of("mensagem", "Usuário deletado com sucesso"));
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
