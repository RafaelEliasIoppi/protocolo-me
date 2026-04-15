package back.backend.controller;

import back.backend.model.Usuario;
import back.backend.model.Role;
import back.backend.service.UsuarioService;
import back.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        try {
            if (usuario.getRole() == null) {
                usuario.setRole(Role.MEDICO);
            }
            Usuario usuarioRegistrado = usuarioService.registrar(usuario);
            Map<String, Object> response = new HashMap<>();
            response.put("id", usuarioRegistrado.getId());
            response.put("email", usuarioRegistrado.getEmail());
            response.put("nome", usuarioRegistrado.getNome());
            response.put("role", usuarioRegistrado.getRole());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciais) {
        try {
            String email = credenciais.get("email");
            String senha = credenciais.get("senha");

            Optional<Usuario> usuarioOpt = usuarioService.findByEmail(email);
            if (usuarioOpt.isEmpty() || !usuarioOpt.get().getAtivo()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", "Usuário não encontrado ou inativo"));
            }

            Usuario usuario = usuarioOpt.get();
            if (!passwordEncoder.matches(senha, usuario.getSenha())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", "Senha incorreta"));
            }

            String token = jwtUtil.gerarToken(usuario.getEmail(), usuario.getRole().name());
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("usuario", Map.of(
                "id", usuario.getId(),
                "email", usuario.getEmail(),
                "nome", usuario.getNome(),
                "role", usuario.getRole()
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("erro", "Erro ao fazer login: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obterUsuario(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.findById(id);
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("erro", "Usuário não encontrado"));
        }
        return ResponseEntity.ok(usuario.get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuarioAtualizado) {
        Optional<Usuario> usuarioOpt = usuarioService.findById(id);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("erro", "Usuário não encontrado"));
        }

        Usuario usuario = usuarioOpt.get();
        if (usuarioAtualizado.getNome() != null) {
            usuario.setNome(usuarioAtualizado.getNome());
        }
        if (usuarioAtualizado.getCrm() != null) {
            usuario.setCrm(usuarioAtualizado.getCrm());
        }
        if (usuarioAtualizado.getCoren() != null) {
            usuario.setCoren(usuarioAtualizado.getCoren());
        }
        if (usuarioAtualizado.getRole() != null) {
            usuario.setRole(usuarioAtualizado.getRole());
        }
        if (usuarioAtualizado.getAtivo() != null) {
            usuario.setAtivo(usuarioAtualizado.getAtivo());
        }

        Usuario usuarioSalvo = usuarioService.atualizar(usuario);
        return ResponseEntity.ok(usuarioSalvo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarUsuario(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.findById(id);
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("erro", "Usuário não encontrado"));
        }
        usuarioService.deletar(id);
        return ResponseEntity.ok(Map.of("mensagem", "Usuário deletado com sucesso"));
    }
}
