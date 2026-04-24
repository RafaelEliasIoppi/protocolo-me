package back.backend.security;

import back.backend.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityJwtIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void limparBaseUsuarios() {
        usuarioRepository.deleteAll();
    }

    @Test
    void deveRetornar401SemTokenEmRotaProtegida() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.erro").value("Não autenticado"));
    }

    @Test
    void deveRetornar403ParaMedicoEmRotaSomenteAdmin() throws Exception {
        registrarUsuarioPublico("medico@example.com", "senha123", "MEDICO");
        String tokenMedico = loginERetornarToken("medico@example.com", "senha123");

        mockMvc.perform(get("/api/usuarios")
                        .header("Authorization", "Bearer " + tokenMedico))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.erro").value("Acesso negado"));
    }

    @Test
    void devePermitirAdminEmRotaAdmin() throws Exception {
        registrarAdmin("admin@example.com", "senha123");
        String tokenAdmin = loginERetornarToken("admin@example.com", "senha123");

        mockMvc.perform(get("/api/usuarios")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk());
    }

    private void registrarUsuarioPublico(String email, String senha, String role) throws Exception {
        Map<String, Object> payload = Map.of(
                "nome", "Usuario Teste",
                "email", email,
                "senha", senha,
                "role", role
        );

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated());
    }

    private void registrarAdmin(String email, String senha) throws Exception {
        Map<String, Object> payload = Map.of(
                "nome", "Admin Teste",
                "email", email,
                "senha", senha,
                "role", "ADMIN"
        );

        mockMvc.perform(post("/api/usuarios/admin/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated());
    }

    private String loginERetornarToken(String email, String senha) throws Exception {
        Map<String, Object> payload = Map.of(
                "email", email,
                "senha", senha
        );

        MvcResult result = mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("token").asText();
    }
}
