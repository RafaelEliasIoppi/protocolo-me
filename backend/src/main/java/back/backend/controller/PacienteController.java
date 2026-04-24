package back.backend.controller;

import back.backend.model.Paciente;
import back.backend.dto.PacienteDTO;
import back.backend.service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pacientes")
@CrossOrigin(origins = "*")
@Transactional(readOnly = true)
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    /**
     * POST /api/pacientes - Criar novo paciente
     */
    @Transactional(readOnly = false)
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Paciente paciente) {
        try {
            Paciente pacienteCriado = pacienteService.criarPaciente(paciente);
            return ResponseEntity.status(HttpStatus.CREATED).body(PacienteDTO.fromEntity(pacienteCriado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Erro ao criar paciente: " + e.getMessage()));
        }
    }

    /**
     * GET /api/pacientes - Listar todos os pacientes
     */
    @GetMapping
    public ResponseEntity<List<PacienteDTO>> listarTodos() {
        List<Paciente> pacientes = pacienteService.listarTodos();
        List<PacienteDTO> dtos = pacientes.stream()
            .map(PacienteDTO::fromEntity)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /api/pacientes/{id} - Obter paciente por ID
     */
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<?> obterPorId(@PathVariable Long id) {
        try {
            Paciente paciente = pacienteService.obterPacientePorId(id);
            return ResponseEntity.ok(PacienteDTO.fromEntity(paciente));
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
            return ResponseEntity.ok(PacienteDTO.fromEntity(paciente));
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
            List<PacienteDTO> dtos = pacientes.stream()
                .map(PacienteDTO::fromEntity)
                .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Hospital não encontrado: " + e.getMessage()));
        }
    }

    /**
     * GET /api/pacientes/status/{status} - Listar pacientes por status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<PacienteDTO>> listarPorStatus(@PathVariable String status) {
        try {
            Paciente.StatusPaciente statusEnum = Paciente.StatusPaciente.valueOf(status.toUpperCase());
            List<Paciente> pacientes = pacienteService.listarPorStatus(statusEnum);
            List<PacienteDTO> dtos = pacientes.stream()
                .map(PacienteDTO::fromEntity)
                .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
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
            List<PacienteDTO> dtos = pacientes.stream()
                .map(PacienteDTO::fromEntity)
                .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Status inválido: " + e.getMessage()));
        }
    }

    /**
     * GET /api/pacientes/em-protocolo-me - Listar apenas pacientes em Protocolo de ME
     */
    @GetMapping("/em-protocolo-me")
    public ResponseEntity<List<PacienteDTO>> listarPacientesEmProtocoloME() {
        List<Paciente> pacientes = pacienteService.listarPacientesEmProtocoloME();
        List<PacienteDTO> dtos = pacientes.stream()
            .map(PacienteDTO::fromEntity)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /api/pacientes/em-protocolo-me/hospital/{hospitalId} - Listar pacientes em Protocolo de ME por hospital
     */
    @GetMapping("/em-protocolo-me/hospital/{hospitalId}")
    public ResponseEntity<?> listarPacientesEmProtocoloMEPorHospital(@PathVariable Long hospitalId) {
        try {
            List<Paciente> pacientes = pacienteService.listarPacientesEmProtocoloMEPorHospital(hospitalId);
            List<PacienteDTO> dtos = pacientes.stream()
                .map(PacienteDTO::fromEntity)
                .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Hospital não encontrado: " + e.getMessage()));
        }
    }

    /**
     * GET /api/pacientes/{id}/relatorio-final - Relatório final completo de um paciente
     */
    @GetMapping("/{id:\\d+}/relatorio-final")
    public ResponseEntity<?> obterRelatorioFinalPaciente(@PathVariable Long id) {
        try {
            PacienteService.RelatorioFinalPaciente relatorio = pacienteService.gerarRelatorioFinalPaciente(id);
            return ResponseEntity.ok(relatorio);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Paciente não encontrado: " + e.getMessage()));
        }
    }

    /**
     * GET /api/pacientes/relatorios-finais - Relatório final de todos os pacientes
     */
    @GetMapping("/relatorios-finais")
    public ResponseEntity<List<PacienteService.RelatorioFinalPaciente>> listarRelatoriosFinaisPacientes() {
        List<PacienteService.RelatorioFinalPaciente> relatorios = pacienteService.gerarRelatoriosFinaisPacientes();
        return ResponseEntity.ok(relatorios);
    }

    /**
     * GET /api/pacientes/buscar?nome={nome} - Procurar pacientes por nome
     */
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarPorNome(@RequestParam String nome) {
        try {
            List<Paciente> pacientes = pacienteService.procurarPorNome(nome);
            List<PacienteDTO> dtos = pacientes.stream()
                .map(PacienteDTO::fromEntity)
                .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
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
            List<PacienteDTO> dtos = pacientes.stream()
                .map(PacienteDTO::fromEntity)
                .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Erro ao buscar: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/pacientes/{id} - Atualizar paciente
     */
    @Transactional(readOnly = false)
    @PutMapping("/{id:\\d+}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Paciente paciente) {
        try {
            Paciente pacienteAtualizado = pacienteService.atualizarPaciente(id, paciente);
            return ResponseEntity.ok(PacienteDTO.fromEntity(pacienteAtualizado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Erro ao atualizar: " + e.getMessage()));
        }
    }

    /**
     * PATCH /api/pacientes/{id}/status - Atualizar status do paciente
     */
    @Transactional(readOnly = false)
    @PatchMapping("/{id:\\d+}/status")
    public ResponseEntity<?> atualizarStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            String statusStr = body.get("status");
            Paciente.StatusPaciente status = Paciente.StatusPaciente.valueOf(statusStr.toUpperCase());
            Paciente pacienteAtualizado = pacienteService.atualizarStatus(id, status);
            return ResponseEntity.ok(PacienteDTO.fromEntity(pacienteAtualizado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Status inválido: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/pacientes/{id} - Deletar paciente
     */
    @Transactional(readOnly = false)
    @DeleteMapping("/{id:\\d+}")
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
