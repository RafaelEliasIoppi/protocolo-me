package back.backend.config;

import back.backend.model.Role;
import back.backend.model.Usuario;
import back.backend.service.UsuarioService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DadosIniciaisConfig {

    private static final Logger logger = LoggerFactory.getLogger(DadosIniciaisConfig.class);

    @Bean
    public CommandLineRunner criarAdminInicial(
            UsuarioService usuarioService,
            PasswordEncoder passwordEncoder,
            @Value("${app.seed.admin-email:admin@protocolo.me}") String adminEmail,
            @Value("${app.seed.admin-password:Admin123!}") String adminPassword,
            @Value("${app.seed.admin-name:Administrador do Sistema}") String adminNome) {

        return args -> {

            // Evita duplicar admin se já existir
            if (usuarioService.countAdmins() > 0) {
                logger.info("Admin já existente, não será recriado.");
                return;
            }

            // Cria usuário admin inicial
            Usuario admin = new Usuario();
            admin.setEmail(adminEmail.trim().toLowerCase());

            // ⚠️ IMPORTANTE: Não codificar aqui! UsuarioService.registrar() já codifica
            admin.setSenha(adminPassword);

            admin.setNome(adminNome);
            admin.setRole(Role.ADMIN);
            admin.setAtivo(true);

            usuarioService.registrar(admin);

            logger.info("Usuário administrador inicial criado com sucesso: {}", adminEmail);
        };
    }
}