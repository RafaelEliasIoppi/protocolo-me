package back.service;

import java.util.List;
import org.springframework.stereotype.Service;
import back.model.Usuario;
import back.repository.UsuarioRepository;




@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario salvar(Usuario usuario) {
        // aqui você pode criptografar a senha antes de salvar
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }
}


