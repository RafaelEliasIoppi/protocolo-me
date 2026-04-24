package back.backend.controller;

import back.backend.exception.RecursoNaoEncontradoException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class GlobalExceptionHandlerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveRetornar404QuandoRecursoNaoEncontrado() throws Exception {
        mockMvc.perform(get("/api/test-errors/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value(404))
                .andExpect(jsonPath("$.mensagem").value("registro nao encontrado"));
    }

    @Test
    void deveRetornar409QuandoConflitoDeRegraNegocio() throws Exception {
        mockMvc.perform(get("/api/test-errors/business-conflict"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value(409))
                .andExpect(jsonPath("$.mensagem").value("conflito de estado"));
    }

    @Test
    void deveRetornar400QuandoFalhaValidacao() throws Exception {
        mockMvc.perform(post("/api/test-errors/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value(400))
                .andExpect(jsonPath("$.mensagem").value("Erro de validação"))
                .andExpect(jsonPath("$.detalhes.nome").value("nome obrigatorio"));
    }

    @Test
    void deveRetornar400QuandoJsonInvalido() throws Exception {
        mockMvc.perform(post("/api/test-errors/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value(400))
                .andExpect(jsonPath("$.mensagem").value("Payload inválido"));
    }

    @Test
    void deveRetornar500QuandoErroInesperado() throws Exception {
        mockMvc.perform(get("/api/test-errors/unexpected"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.codigo").value(500))
                .andExpect(jsonPath("$.mensagem").value("Erro interno no servidor"));
    }

    @TestConfiguration
    static class TestControllerConfig {
        @Bean
        TestErrorController testErrorController() {
            return new TestErrorController();
        }
    }

    @RestController
    @RequestMapping("/api/test-errors")
    @Validated
    static class TestErrorController {

        @GetMapping("/not-found")
        public String notFound() {
            throw new RecursoNaoEncontradoException("registro nao encontrado");
        }

        @GetMapping("/business-conflict")
        public String businessConflict() {
            throw new IllegalStateException("conflito de estado");
        }

        @PostMapping("/validate")
        public String validate(@Valid @RequestBody TestRequest request) {
            return request.getNome();
        }

        @GetMapping("/unexpected")
        public String unexpected() {
            throw new NullPointerException("erro inesperado");
        }
    }

    static class TestRequest {
        @NotBlank(message = "nome obrigatorio")
        private String nome;

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }
    }
}
