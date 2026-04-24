package back.backend.controller;

import back.backend.dto.*;
import back.backend.dto.UsuarioRequestDTO;
import back.backend.mapper.UsuarioRequestMapper;
import back.backend.service.UsuarioService;
import back.backend.security.JwtUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;
    private final UsuarioRequestMapper usuarioRequestMapper;

    // =========================
    // REGISTRO PÚBLICO
    // =========================
    @PostMapping
    public ResponseEntity<UsuarioDTO> registrar(@Valid @RequestBody UsuarioRequestDTO request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioService.registrarPublico(usuarioRequestMapper.toEntity(request)));
    }

    // =========================
    // REGISTRO ADMIN
    // =========================
    @PostMapping("/admin/registrar")
    public ResponseEntity<UsuarioDTO> registrarAdmin(@Valid @RequestBody UsuarioRequestDTO request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioService.registrarAdmin(usuarioRequestMapper.toEntity(request)));
    }

    // =========================
    // LOGIN
    // =========================
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {

        UsuarioDTO usuario = usuarioService.autenticar(request.getEmail(), request.getSenha());

        String token = jwtUtil.gerarToken(usuario.getEmail(), usuario.getRole());
        long expiraEm = jwtUtil.extractExpiration(token).getTime();

        return ResponseEntity.ok(
            new AuthResponseDTO(token, expiraEm, usuario)
        );
    }

    // =========================
    // MINHA SENHA
    // =========================
    @PatchMapping("/minha-senha")
    public ResponseEntity<AcaoResponseDTO> alterarMinhaSenha(@Valid @RequestBody AlterarSenhaDTO dto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        UsuarioDTO usuario = usuarioService.alterarMinhaSenha(
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
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    // =========================
    // ATUALIZAR
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> atualizarUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO request) {

        var dados = usuarioRequestMapper.toEntity(request);

        UsuarioDTO atualizado = usuarioService.atualizarUsuario(id, dados);

        return ResponseEntity.ok(atualizado);
    }

    // =========================
    // RESET SENHA
    // =========================
    @PatchMapping("/{id}/senha")
    public ResponseEntity<AcaoResponseDTO> redefinirSenha(@PathVariable Long id,
                                                         @Valid @RequestBody ResetSenhaDTO dto) {

        UsuarioDTO usuario = usuarioService.redefinirSenha(id, dto.getSenhaNova());

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
}