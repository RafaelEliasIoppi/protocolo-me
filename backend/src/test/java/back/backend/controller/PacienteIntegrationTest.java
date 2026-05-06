package back.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
public class PacienteIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void criarHospitalCriarPacienteEListarPorStatus() throws Exception {

        // 1) Criar hospital
        Map<String, Object> hospital = Map.of(
                "nome", "Hospital Teste",
                "cnpj", "00.000.000/0001-01",
                "cidade", "Cidade X",
                "estado", "ST",
                "status", "ATIVO",
                "telefone", "(11) 99999-0000",
                "endereco", "Rua Teste, 123",
                "email", "contato@hospitalteste.example");
        MvcResult hospResult = mockMvc.perform(post("/api/hospitais")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(hospital)))
                .andExpect(status().isCreated())
                .andReturn();

        String hospBody = hospResult.getResponse().getContentAsString();
        Map<String, Object> hospMap = objectMapper.readValue(hospBody, Map.class);
        Number hospId = (Number) hospMap.get("id");

        // 2) Criar paciente com status INTERNADO
        Map<String, Object> paciente = Map.of(
                "nome", "Paciente Teste",
                "cpf", "12345678901",
                "dataNascimento", LocalDate.of(1980, 1, 1).toString(),
                "genero", "MASCULINO",
                "hospitalId", hospId.longValue(),
                "status", "INTERNADO");
        mockMvc.perform(post("/api/pacientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paciente)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Paciente Teste"))
                .andExpect(jsonPath("$.cpf").value("12345678901"));

        // 3) Listar por status INTERNADO e validar que contém o paciente
        mockMvc.perform(get("/api/pacientes/status/INTERNADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(not(empty()))))
                .andExpect(jsonPath("$[?(@.cpf=='12345678901')]").exists());
    }
}
