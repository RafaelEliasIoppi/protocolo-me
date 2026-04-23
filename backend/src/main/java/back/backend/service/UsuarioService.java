package back.backend.service;

import back.backend.model.Usuario;
import back.backend.model.Role;
import back.backend.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // =====================================================
    // SPRING SECURITY
    // =====================================================
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        String emailNormalizado = normalizarEmail(email);

        Usuario usuario = usuarioRepository.findByEmail(emailNormalizado)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new UsernameNotFoundException("Usuário inativo");
        }

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getSenha())
                .authorities("ROLE_" + usuario.getRole().name())
                .build();
    }

    // =====================================================
    // REGISTRO
    // =====================================================
    public Usuario registrar(Usuario usuario) {

        validarUsuarioParaRegistro(usuario);

        String email = normalizarEmail(usuario.getEmail());

        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }

        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha().trim()));

        if (usuario.getRole() == null) {
            usuario.setRole(Role.MEDICO);
        }

        usuario.setAtivo(true);
        usuario.setDataCriacao(LocalDateTime.now());
        usuario.setDataAtualizacao(LocalDateTime.now());

        return usuarioRepository.save(usuario);
    }

    // =====================================================
    // CONSULTAS
    // =====================================================
    public long countUsuarios() {
        return usuarioRepository.count();
    }

    public long countAdmins() {
        return usuarioRepository.countByRole(Role.ADMIN);
    }

    public Optional<Usuario> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return usuarioRepository.findByEmail(normalizarEmail(email));
    }

    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    // =====================================================
    // ATUALIZAÇÃO
    // =====================================================
    public Usuario atualizarUsuario(Long id, Usuario dados) {

        Usuario usuario = buscarUsuarioOuFalhar(id);

        atualizarEmail(usuario, dados);
        atualizarNome(usuario, dados);
        atualizarRole(usuario, dados);
        atualizarStatus(usuario, dados);
        atualizarCrm(usuario, dados);
        atualizarCoren(usuario, dados);

        usuario.setDataAtualizacao(LocalDateTime.now());

        return usuarioRepository.save(usuario);
    }

    // =====================================================
    // SENHAS
    // =====================================================
    public Usuario redefinirSenha(Long id, String senhaNova) {

        Usuario usuario = buscarUsuarioOuFalhar(id);

        validarSenhaNova(senhaNova);

        usuario.setSenha(passwordEncoder.encode(senhaNova.trim()));
        usuario.setDataAtualizacao(LocalDateTime.now());

        return usuarioRepository.save(usuario);
    }

    public Usuario alterarMinhaSenha(String email, String senhaAtual, String senhaNova, String confirmarSenha) {

        Usuario usuario = usuarioRepository.findByEmail(normalizarEmail(email))
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (senhaAtual == null || senhaAtual.isBlank()) {
            throw new RuntimeException("Informe a senha atual");
        }

        validarSenhaNova(senhaNova);

        if (!senhaNova.trim().equals(confirmarSenha != null ? confirmarSenha.trim() : "")) {
            throw new RuntimeException("Confirmação de senha não confere");
        }

        if (!passwordEncoder.matches(senhaAtual, usuario.getSenha())) {
            throw new RuntimeException("Senha atual incorreta");
        }

        if (passwordEncoder.matches(senhaNova.trim(), usuario.getSenha())) {
            throw new RuntimeException("Nova senha deve ser diferente da atual");
        }

        usuario.setSenha(passwordEncoder.encode(senhaNova.trim()));
        usuario.setDataAtualizacao(LocalDateTime.now());

        return usuarioRepository.save(usuario);
    }

    // =====================================================
    // DELETE
    // =====================================================
    public void deletar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    // =====================================================
    // MÉTODOS PRIVADOS (PROFISSIONAL)
    // =====================================================

    private Usuario buscarUsuarioOuFalhar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    private String normalizarEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new RuntimeException("Email é obrigatório");
        }
        return email.trim().toLowerCase();
    }

    private void validarUsuarioParaRegistro(Usuario usuario) {
        if (usuario == null) {
            throw new RuntimeException("Usuário inválido");
        }

        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new RuntimeException("Email é obrigatório");
        }

        validarSenhaNova(usuario.getSenha());
    }

    private void validarSenhaNova(String senha) {
        if (senha == null || senha.trim().length() < 6) {
            throw new RuntimeException("Senha deve ter pelo menos 6 caracteres");
        }
    }

    private void atualizarEmail(Usuario usuario, Usuario dados) {
        if (dados.getEmail() != null && !dados.getEmail().trim().isEmpty()) {

            String novoEmail = normalizarEmail(dados.getEmail());

            usuarioRepository.findByEmail(novoEmail).ifPresent(existente -> {
                if (!existente.getId().equals(usuario.getId())) {
                    throw new RuntimeException("Email já cadastrado");
                }
            });

            usuario.setEmail(novoEmail);
        }
    }

    private void atualizarNome(Usuario usuario, Usuario dados) {
        if (dados.getNome() != null && !dados.getNome().trim().isEmpty()) {
            usuario.setNome(dados.getNome().trim());
        }
    }

    private void atualizarRole(Usuario usuario, Usuario dados) {
        if (dados.getRole() != null) {
            usuario.setRole(dados.getRole());
        }
    }

    private void atualizarStatus(Usuario usuario, Usuario dados) {
        if (dados.getAtivo() != null) {
            usuario.setAtivo(dados.getAtivo());
        }
    }

    private void atualizarCrm(Usuario usuario, Usuario dados) {
        if (dados.getCrm() != null && !dados.getCrm().trim().isEmpty()) {
            usuario.setCrm(dados.getCrm().trim());
        }
    }

    private void atualizarCoren(Usuario usuario, Usuario dados) {
        if (dados.getCoren() != null && !dados.getCoren().trim().isEmpty()) {
            usuario.setCoren(dados.getCoren().trim());
        }
    }
}