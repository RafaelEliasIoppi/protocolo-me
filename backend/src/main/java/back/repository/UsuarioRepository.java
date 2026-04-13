package back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import back.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}