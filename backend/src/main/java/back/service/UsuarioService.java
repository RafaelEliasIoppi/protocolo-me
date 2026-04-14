package back.service;

import java.util.List;
import java.util.Optional;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import back.model.Usuario;
import back.repository.UsuarioRepository;
import back.security.JwUtil;




@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwUtil jwUtil;

    public UsuarioService(UsuarioRepository usuarioRepository, JwUtil jwUtil) {
        this.usuarioRepository = usuarioRepository;
        this.jwUtil = jwUtil;
    }

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

    public String authenticate(String email, String senha) {
        UserDetails userDetails = loadUserByUsername(email);
        if (passwordEncoder.matches(senha, userDetails.getPassword())) {
            return jwUtil.generateToken(userDetails);
        } else {
            throw new RuntimeException("Credenciais inválidas");
        }
    }

    public Usuario salvar(Usuario usuario) {
        // Criptografar a senha antes de salvar
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    public Usuario atualizar(Long id, Usuario usuario) {
        Usuario existente = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        existente.setNome(usuario.getNome());
        existente.setEmail(usuario.getEmail());
        existente.setRole(usuario.getRole());
        return usuarioRepository.save(existente);
    }

    public void deletar(Long id) {
        usuarioRepository.deleteById(id);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }
}


