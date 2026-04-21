package back.backend.service;

import back.backend.model.Usuario;
import back.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
        
        return User.builder()
            .username(usuario.getEmail())
            .password(usuario.getSenha())
            .roles(usuario.getRole().name())
            .build();
    }

    public Usuario registrar(Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario.setDataCriacao(LocalDateTime.now());
        usuario.setDataAtualizacao(LocalDateTime.now());
        usuario.setAtivo(true);
        return usuarioRepository.save(usuario);
    }

    public long countUsuarios() {
        return usuarioRepository.count();
    }

    public long countAdmins() {
        return usuarioRepository.countByRole(back.backend.model.Role.ADMIN);
    }

    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario atualizarUsuario(Long id, Usuario usuarioAtualizado) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (usuarioAtualizado.getEmail() != null && !usuarioAtualizado.getEmail().trim().isEmpty()) {
            String emailNovo = usuarioAtualizado.getEmail().trim();
            usuarioRepository.findByEmail(emailNovo).ifPresent(existente -> {
                if (!existente.getId().equals(id)) {
                    throw new RuntimeException("Email já cadastrado");
                }
            });
            usuario.setEmail(emailNovo);
        }

        if (usuarioAtualizado.getNome() != null && !usuarioAtualizado.getNome().trim().isEmpty()) {
            usuario.setNome(usuarioAtualizado.getNome().trim());
        }

        if (usuarioAtualizado.getRole() != null) {
            usuario.setRole(usuarioAtualizado.getRole());
        }

        if (usuarioAtualizado.getAtivo() != null) {
            usuario.setAtivo(usuarioAtualizado.getAtivo());
        }

        if (usuarioAtualizado.getCrm() != null) {
            usuario.setCrm(usuarioAtualizado.getCrm());
        }

        if (usuarioAtualizado.getCoren() != null) {
            usuario.setCoren(usuarioAtualizado.getCoren());
        }

        usuario.setDataAtualizacao(LocalDateTime.now());
        return usuarioRepository.save(usuario);
    }

    public Usuario redefinirSenha(Long id, String senhaNova) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (senhaNova == null || senhaNova.trim().length() < 6) {
            throw new RuntimeException("Nova senha deve ter pelo menos 6 caracteres");
        }

        usuario.setSenha(passwordEncoder.encode(senhaNova.trim()));
        usuario.setDataAtualizacao(LocalDateTime.now());
        return usuarioRepository.save(usuario);
    }

    public Usuario alterarMinhaSenha(String email, String senhaAtual, String senhaNova, String confirmarSenha) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (senhaAtual == null || senhaAtual.trim().isEmpty()) {
            throw new RuntimeException("Informe a senha atual");
        }

        if (senhaNova == null || senhaNova.trim().length() < 6) {
            throw new RuntimeException("A nova senha deve ter pelo menos 6 caracteres");
        }

        if (confirmarSenha == null || !senhaNova.trim().equals(confirmarSenha.trim())) {
            throw new RuntimeException("A nova senha e a confirmação não conferem");
        }

        if (!passwordEncoder.matches(senhaAtual, usuario.getSenha())) {
            throw new RuntimeException("Senha atual incorreta");
        }

        if (passwordEncoder.matches(senhaNova.trim(), usuario.getSenha())) {
            throw new RuntimeException("A nova senha deve ser diferente da senha atual");
        }

        usuario.setSenha(passwordEncoder.encode(senhaNova.trim()));
        usuario.setDataAtualizacao(LocalDateTime.now());
        return usuarioRepository.save(usuario);
    }

    public Usuario atualizar(Usuario usuario) {
        usuario.setDataAtualizacao(LocalDateTime.now());
        return usuarioRepository.save(usuario);
    }

    public void deletar(Long id) {
        usuarioRepository.deleteById(id);
    }
}
