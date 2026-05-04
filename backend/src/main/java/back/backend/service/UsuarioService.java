package back.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import back.backend.dto.UsuarioDTO;
import back.backend.exception.AutenticacaoException;
import back.backend.exception.ConflitoNegocioException;
import back.backend.exception.RecursoNaoEncontradoException;
import back.backend.mapper.UsuarioMapper;
import back.backend.model.Role;
import back.backend.model.Usuario;
import back.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;

    @Value("${app.seed.admin-email:admin@protocolo.me}")
    private String adminPrincipalEmail;

    // ================= ADMIN =================

    public long countAdmins() {
        return usuarioRepository.countByRole(Role.ADMIN);
    }

    public void validarPermissaoCriacaoAdmin(Usuario usuario) {

        long totalAdmins = usuarioRepository.countByRole(Role.ADMIN);

        // Se não existe nenhum admin e o usuário a ser criado é ADMIN,
        // permitir bootstrap (primeiro admin) mesmo sem autenticação.
        if (totalAdmins == 0 && usuario.getRole() == Role.ADMIN) {
            return;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = auth != null
                && auth.isAuthenticated()
                && auth.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isCentral = auth != null
                && auth.isAuthenticated()
                && auth.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals(
                                "ROLE_CENTRAL_TRANSPLANTES"));

        // PRIMEIRO USUÁRIO OBRIGATORIAMENTE ADMIN
        if (totalAdmins == 0 && usuario.getRole() != Role.ADMIN) {
            throw new ConflitoNegocioException(
                    "Primeiro usuário deve ser ADMIN");
        }

        // ADMIN pode criar qualquer perfil
        if (isAdmin) {
            return;
        }

        // CENTRAL pode criar apenas MEDICO e ENFERMEIRO
        if (isCentral) {

            if (usuario.getRole() == Role.MEDICO ||
                    usuario.getRole() == Role.ENFERMEIRO) {
                return;
            }

            throw new ConflitoNegocioException(
                    "CENTRAL_TRANSPLANTES pode criar apenas MÉDICO ou ENFERMEIRO");
        }

        throw new ConflitoNegocioException(
                "Sem permissão para cadastrar usuários");
    }

    // ================= AUTH =================

    public UsuarioDTO autenticar(String email, String senha) {

        String emailNormalizado = normalizarEmail(email);

        Usuario usuario = usuarioRepository.findByEmail(emailNormalizado)
                .orElseThrow(() -> new AutenticacaoException("Usuário não encontrado ou inativo"));

        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new AutenticacaoException("Usuário não encontrado ou inativo");
        }

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new AutenticacaoException("Senha incorreta");
        }

        return toDTO(usuario);
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

    // ================= CREATE =================

    public UsuarioDTO registrar(Usuario usuario) {

        validarUsuario(usuario);

        String email = normalizarEmail(usuario.getEmail());

        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new ConflitoNegocioException("Email já cadastrado");
        }

        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        if (usuario.getRole() == null) {
            usuario.setRole(Role.MEDICO);
        }

        usuario.setAtivo(true);
        usuario.setDataCriacao(LocalDateTime.now());
        usuario.setDataAtualizacao(LocalDateTime.now());

        return toDTO(usuarioRepository.save(usuario));
    }

    public UsuarioDTO registrarAdmin(Usuario usuario) {

        if (usuario.getRole() == null) {
            throw new IllegalArgumentException("Informe a função");
        }

        validarPermissaoCriacaoAdmin(usuario);

        return registrar(usuario);
    }

    // ================= UPDATE =================

    public UsuarioDTO atualizarUsuario(Long id, Usuario dados) {

        Usuario usuario = buscarOuFalhar(id);

        if (Boolean.FALSE.equals(dados.getAtivo()) && isAdminPrincipal(usuario)) {
            throw new ConflitoNegocioException("Não é possível desativar o admin principal");
        }

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

        return toDTO(usuarioRepository.save(usuario));
    }

    public UsuarioDTO redefinirSenha(Long id, String senhaNova) {

        Usuario usuario = buscarOuFalhar(id);

        validarSenha(senhaNova);

        usuario.setSenha(passwordEncoder.encode(senhaNova));
        usuario.setDataAtualizacao(LocalDateTime.now());

        return toDTO(usuarioRepository.save(usuario));
    }

    public UsuarioDTO alterarMinhaSenha(
            String email,
            String atual,
            String nova,
            String confirmar) {

        Usuario usuario = usuarioRepository.findByEmail(normalizarEmail(email))
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        if (!passwordEncoder.matches(atual, usuario.getSenha())) {
            throw new IllegalArgumentException("Senha atual inválida");
        }

        if (!nova.equals(confirmar)) {
            throw new IllegalArgumentException("Confirmação inválida");
        }

        validarSenha(nova);

        usuario.setSenha(passwordEncoder.encode(nova));
        usuario.setDataAtualizacao(LocalDateTime.now());

        return toDTO(usuarioRepository.save(usuario));
    }

    // ================= READ =================

    public List<UsuarioDTO> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // ================= DELETE =================

    public void deletar(@NonNull Long id) {

        if (!usuarioRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Usuário não encontrado");
        }

        usuarioRepository.deleteById(id);
    }

    // ================= HELPERS =================

    private Usuario buscarOuFalhar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
    }

    private String normalizarEmail(String email) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email obrigatório");
        }

        return email.trim().toLowerCase();
    }

    private void validarUsuario(Usuario usuario) {

        if (usuario.getSenha() == null ||
                usuario.getSenha().length() < 6) {
            throw new IllegalArgumentException("Senha inválida");
        }
    }

    private void validarSenha(String senha) {

        if (senha == null || senha.length() < 6) {
            throw new IllegalArgumentException(
                    "Senha deve ter no mínimo 6 caracteres");
        }
    }

    private boolean isAdminPrincipal(Usuario usuario) {
        return usuario.getRole() == Role.ADMIN
                && usuario.getEmail() != null
                && usuario.getEmail().equalsIgnoreCase(adminPrincipalEmail);
    }

    private UsuarioDTO toDTO(Usuario usuario) {
        return usuarioMapper.toDTO(usuario);
    }
}
