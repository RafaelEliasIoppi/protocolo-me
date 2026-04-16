package back.backend.controller;

import back.backend.model.Paciente;
import back.backend.service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    /**
     * POST /api/pacientes - Criar novo paciente
     */
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Paciente paciente) {
        try {
            Paciente pacienteCriado = pacienteService.criarPaciente(paciente);
            return ResponseEntity.status(HttpStatus.CREATED).body(pacienteCriado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Erro ao criar paciente: " + e.getMessage()));
        }
    }

    /**
     * GET /api/pacientes - Listar todos os pacientes
     */
    @GetMapping
    public ResponseEntity<List<Paciente>> listarTodos() {
        List<Paciente> pacientes = pacienteService.listarTodos();
        return ResponseEntity.ok(pacientes);
    }

    /**
     * GET /api/pacientes/{id} - Obter paciente por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obterPorId(@PathVariable Long id) {
        try {
            Paciente paciente = pacienteService.obterPacientePorId(id);
            return ResponseEntity.ok(paciente);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Paciente não encontrado: " + e.getMessage()));
        }
    }

    /**
     * GET /api/pacientes/cpf/{cpf} - Obter paciente por CPF
     */
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<?> obterPorCpf(@PathVariable String cpf) {
        try {
            Paciente paciente = pacienteService.obterPacientePorCpf(cpf);
            return ResponseEntity.ok(paciente);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Paciente não encontrado: " + e.getMessage()));
        }
    }

    /**
     * GET /api/pacientes/hospital/{hospitalId} - Listar pacientes por hospital
     */
    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<?> listarPorHospital(@PathVariable Long hospitalId) {
        try {
            List<Paciente> pacientes = pacienteService.listarPorHospital(hospitalId);
            return ResponseEntity.ok(pacientes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Hospital não encontrado: " + e.getMessage()));
        }
    }

    /**
     * GET /api/pacientes/status/{status} - Listar pacientes por status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Paciente>> listarPorStatus(@PathVariable String status) {
        try {
            Paciente.StatusPaciente statusEnum = Paciente.StatusPaciente.valueOf(status.toUpperCase());
            List<Paciente> pacientes = pacienteService.listarPorStatus(statusEnum);
            return ResponseEntity.ok(pacientes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(null);
        }
    }

    /**
     * GET /api/pacientes/hospital/{hospitalId}/status/{status} - Listar por hospital e status
     */
    @GetMapping("/hospital/{hospitalId}/status/{status}")
    public ResponseEntity<?> listarPorHospitalEStatus(
            @PathVariable Long hospitalId,
            @PathVariable String status) {
        try {
            Paciente.StatusPaciente statusEnum = Paciente.StatusPaciente.valueOf(status.toUpperCase());
            List<Paciente> pacientes = pacienteService.listarPorHospitalEStatus(hospitalId, statusEnum);
            return ResponseEntity.ok(pacientes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Status inválido: " + e.getMessage()));
        }
    }

    /**
     * GET /api/pacientes/buscar?nome={nome} - Procurar pacientes por nome
     */
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarPorNome(@RequestParam String nome) {
        try {
            List<Paciente> pacientes = pacienteService.procurarPorNome(nome);
            return ResponseEntity.ok(pacientes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Erro ao buscar: " + e.getMessage()));
        }
    }

    /**
     * GET /api/pacientes/hospital/{hospitalId}/buscar?nome={nome} - Procurar por nome no hospital
     */
    @GetMapping("/hospital/{hospitalId}/buscar")
    public ResponseEntity<?> buscarPorNomeEHospital(
            @PathVariable Long hospitalId,
            @RequestParam String nome) {
        try {
            List<Paciente> pacientes = pacienteService.procurarPorNomeEHospital(hospitalId, nome);
            return ResponseEntity.ok(pacientes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Erro ao buscar: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/pacientes/{id} - Atualizar paciente
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Paciente paciente) {
        try {
            Paciente pacienteAtualizado = pacienteService.atualizarPaciente(id, paciente);
            return ResponseEntity.ok(pacienteAtualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Erro ao atualizar: " + e.getMessage()));
        }
    }

    /**
     * PATCH /api/pacientes/{id}/status - Atualizar status do paciente
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            String statusStr = body.get("status");
            Paciente.StatusPaciente status = Paciente.StatusPaciente.valueOf(statusStr.toUpperCase());
            Paciente pacienteAtualizado = pacienteService.atualizarStatus(id, status);
            return ResponseEntity.ok(pacienteAtualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Status inválido: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/pacientes/{id} - Deletar paciente
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            pacienteService.deletarPaciente(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Paciente não encontrado: " + e.getMessage()));
        }
    }

    /**
     * GET /api/pacientes/estatisticas - Obter estatísticas de pacientes
     */
    @GetMapping("/estatisticas/resumo")
    public ResponseEntity<PacienteService.PacienteStatisticas> obterEstatisticas() {
        PacienteService.PacienteStatisticas stats = pacienteService.obterEstatisticas();
        return ResponseEntity.ok(stats);
    }

    // Inner class para erro
    public static class ErrorResponse {
        private String mensagem;

        public ErrorResponse(String mensagem) {
            this.mensagem = mensagem;
        }

        public String getMensagem() { return mensagem; }
        public void setMensagem(String mensagem) { this.mensagem = mensagem; }
    }

}
