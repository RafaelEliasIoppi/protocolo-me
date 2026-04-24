package back.backend.controller;

import back.backend.model.Usuario;
import back.backend.model.Role;
import back.backend.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UsuarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String API_USUARIOS = "/api/usuarios";
    private static final String API_LOGIN = "/api/usuarios/login";

    @BeforeEach
    public void setUp() {
        usuarioRepository.deleteAll();
    }

    @Test
    public void testCadastroUsuarioComSucesso() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNome("João Silva");
        usuario.setEmail("joao@example.com");
        usuario.setSenha("senha123");
        usuario.setRole(Role.MEDICO);

        MvcResult result = mockMvc.perform(post(API_USUARIOS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.email").value("joao@example.com"))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.role").value("MEDICO"))
                .andReturn();

        System.out.println("✓ Cadastro bem-sucedido: " + result.getResponse().getContentAsString());
    }

    @Test
    public void testCadastroComEmailDuplicado() throws Exception {
        Usuario usuario1 = new Usuario();
        usuario1.setNome("João Silva");
        usuario1.setEmail("joao@example.com");
        usuario1.setSenha("senha123");
        usuario1.setRole(Role.MEDICO);

        // Primeiro cadastro
        mockMvc.perform(post(API_USUARIOS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario1)))
                .andExpect(status().isCreated());

        // Segundo cadastro com mesmo email
        mockMvc.perform(post(API_USUARIOS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario1)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem").value("Email já cadastrado"))
                .andExpect(jsonPath("$.codigo").value(409));

        System.out.println("✓ Validação de email duplicado passou");
    }

    @Test
    public void testLoginComCredenciaisValidas() throws Exception {
        // Criar usuário primeiro
        Usuario usuario = new Usuario();
        usuario.setNome("Maria Santos");
        usuario.setEmail("maria@example.com");
        usuario.setSenha("senha456");
        usuario.setRole(Role.ENFERMEIRO);

        mockMvc.perform(post(API_USUARIOS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isCreated());

        // Fazer login
        Map<String, String> credenciais = new HashMap<>();
        credenciais.put("email", "maria@example.com");
        credenciais.put("senha", "senha456");

        MvcResult result = mockMvc.perform(post(API_LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credenciais)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.usuario.email").value("maria@example.com"))
                .andExpect(jsonPath("$.usuario.nome").value("Maria Santos"))
                .andExpect(jsonPath("$.usuario.role").value("ENFERMEIRO"))
                .andReturn();

        System.out.println("✓ Login bem-sucedido: token gerado");
    }

    @Test
    public void testLoginComSenhaIncorreta() throws Exception {
        // Criar usuário
        Usuario usuario = new Usuario();
        usuario.setNome("Carlos Oliveira");
        usuario.setEmail("carlos@example.com");
        usuario.setSenha("senha789");
        usuario.setRole(Role.MEDICO);

        mockMvc.perform(post(API_USUARIOS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isCreated());

        // Tentar login com senha errada
        Map<String, String> credenciais = new HashMap<>();
        credenciais.put("email", "carlos@example.com");
        credenciais.put("senha", "senhaErrada");

        mockMvc.perform(post(API_LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credenciais)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensagem").value("Senha incorreta"))
                .andExpect(jsonPath("$.codigo").value(401));

        System.out.println("✓ Rejeição de senha incorreta passou");
    }

    @Test
    public void testLoginComEmailNaoRegistrado() throws Exception {
        Map<String, String> credenciais = new HashMap<>();
        credenciais.put("email", "naoexiste@example.com");
        credenciais.put("senha", "qualquerSenha");

        mockMvc.perform(post(API_LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credenciais)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensagem").value("Usuário não encontrado ou inativo"))
                .andExpect(jsonPath("$.codigo").value(401));

        System.out.println("✓ Rejeição de usuário não registrado passou");
    }

    @Test
    public void testCadastroComRoleAutomatica() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNome("Ana Costa");
        usuario.setEmail("ana@example.com");
        usuario.setSenha("senha321");
        // Não definir role

        mockMvc.perform(post(API_USUARIOS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("MEDICO"));

        System.out.println("✓ Role automática (MEDICO) atribuída com sucesso");
    }

        @Test
        public void testBootstrapPrimeiroAdminMesmoComUsuarioComumExistente() throws Exception {
                Usuario usuarioComum = new Usuario();
                usuarioComum.setNome("Usuario Comum");
                usuarioComum.setEmail("comum@example.com");
                usuarioComum.setSenha("senha123");
                usuarioComum.setRole(Role.MEDICO);

                mockMvc.perform(post(API_USUARIOS)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(usuarioComum)))
                                .andExpect(status().isCreated());

                Usuario admin = new Usuario();
                admin.setNome("Admin Inicial");
                admin.setEmail("admin@example.com");
                admin.setSenha("senhaAdmin123");
                admin.setRole(Role.ADMIN);

                mockMvc.perform(post(API_USUARIOS + "/admin/registrar")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(admin)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.role").value("ADMIN"));

                System.out.println("✓ Bootstrap de primeiro admin com usuários comuns existentes passou");
        }

    @Test
    public void testFluxoCompletoRegistroELogin() throws Exception {
        // 1. Cadastro
        Usuario usuario = new Usuario();
        usuario.setNome("Pedro Gomes");
        usuario.setEmail("pedro@example.com");
        usuario.setSenha("senhaForte123");
        usuario.setRole(Role.MEDICO);

        MvcResult cadastroResult = mockMvc.perform(post(API_USUARIOS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isCreated())
                .andReturn();

        String cadastroResponse = cadastroResult.getResponse().getContentAsString();
        System.out.println("Cadastro realizado: " + cadastroResponse);

        // 2. Login imediatamente depois
        Map<String, String> credenciais = new HashMap<>();
        credenciais.put("email", "pedro@example.com");
        credenciais.put("senha", "senhaForte123");

        MvcResult loginResult = mockMvc.perform(post(API_LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credenciais)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andReturn();

        String loginResponse = loginResult.getResponse().getContentAsString();
        System.out.println("Login realizado com sucesso");
        System.out.println("Response: " + loginResponse);

        // Extrair token
        Map<String, Object> loginMap = objectMapper.readValue(loginResponse, Map.class);
        String token = (String) loginMap.get("token");
        System.out.println("✓ Token gerado: " + token.substring(0, 20) + "...");
    }

}
