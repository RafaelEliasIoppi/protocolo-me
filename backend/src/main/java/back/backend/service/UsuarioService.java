package back.backend.service;

import back.backend.model.Role;
import back.backend.model.Usuario;
import back.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public long countAdmins() {
        return usuarioRepository.countByRole(Role.ADMIN);
    }

    public Usuario autenticar(String email, String senha) {

        String emailNormalizado = normalizarEmail(email);

        Usuario usuario = usuarioRepository.findByEmail(emailNormalizado)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new RuntimeException("Usuário inativo");
        }

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new RuntimeException("Senha inválida");
        }

        return usuario;
    }

    public void validarPermissaoCriacaoAdmin(Usuario usuario) {

        long totalAdmins = usuarioRepository.countByRole(Role.ADMIN);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = auth != null
                && auth.isAuthenticated()
                && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (totalAdmins == 0 && usuario.getRole() != Role.ADMIN) {
            throw new RuntimeException("Primeiro usuário deve ser ADMIN");
        }

        if (totalAdmins > 0 && !isAdmin) {
            throw new RuntimeException("Apenas ADMIN pode cadastrar usuários");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) {

        Usuario usuario = usuarioRepository.findByEmail(normalizarEmail(email))
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getSenha())
                .authorities("ROLE_" + usuario.getRole().name())
                .build();
    }

    public Usuario registrar(Usuario usuario) {

        validarUsuario(usuario);

        String email = normalizarEmail(usuario.getEmail());

        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }

        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        if (usuario.getRole() == null) {
            usuario.setRole(Role.MEDICO);
        }

        usuario.setAtivo(true);
        usuario.setDataCriacao(LocalDateTime.now());
        usuario.setDataAtualizacao(LocalDateTime.now());

        return usuarioRepository.save(usuario);
    }

    public Usuario atualizarUsuario(Long id, Usuario dados) {

        Usuario usuario = buscarOuFalhar(id);

        if (dados.getEmail() != null) {
            usuario.setEmail(normalizarEmail(dados.getEmail()));
        }

        if (dados.getNome() != null) {
            usuario.setNome(dados.getNome());
        }

        if (dados.getRole() != null) {
            usuario.setRole(dados.getRole());
        }

        if (dados.getAtivo() != null) {
            usuario.setAtivo(dados.getAtivo());
        }

        usuario.setDataAtualizacao(LocalDateTime.now());

        return usuarioRepository.save(usuario);
    }

    public Usuario redefinirSenha(Long id, String senhaNova) {

        Usuario usuario = buscarOuFalhar(id);

        validarSenha(senhaNova);

        usuario.setSenha(passwordEncoder.encode(senhaNova));
        usuario.setDataAtualizacao(LocalDateTime.now());

        return usuarioRepository.save(usuario);
    }

    public Usuario alterarMinhaSenha(String email, String atual, String nova, String confirmar) {

        Usuario usuario = usuarioRepository.findByEmail(normalizarEmail(email))
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(atual, usuario.getSenha())) {
            throw new RuntimeException("Senha atual inválida");
        }

        if (!nova.equals(confirmar)) {
            throw new RuntimeException("Confirmação inválida");
        }

        validarSenha(nova);

        usuario.setSenha(passwordEncoder.encode(nova));
        usuario.setDataAtualizacao(LocalDateTime.now());

        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public void deletar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    private Usuario buscarOuFalhar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    private String normalizarEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new RuntimeException("Email obrigatório");
        }
        return email.trim().toLowerCase();
    }

    private void validarUsuario(Usuario usuario) {
        if (usuario.getSenha() == null || usuario.getSenha().length() < 6) {
            throw new RuntimeException("Senha inválida");
        }
    }

    private void validarSenha(String senha) {
        if (senha == null || senha.length() < 6) {
            throw new RuntimeException("Senha deve ter no mínimo 6 caracteres");
        }
    }
}