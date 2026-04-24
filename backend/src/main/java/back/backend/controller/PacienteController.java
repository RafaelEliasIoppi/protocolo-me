package back.backend.controller;

import back.backend.model.Paciente;
import back.backend.dto.PacienteDTO;
import back.backend.service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    @PostMapping
    public ResponseEntity<PacienteDTO> criar(@RequestBody Paciente paciente) {
        Paciente pacienteCriado = pacienteService.criarPaciente(paciente);
        return ResponseEntity.status(HttpStatus.CREATED).body(PacienteDTO.fromEntity(pacienteCriado));
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
    public ResponseEntity<PacienteDTO> obterPorId(@PathVariable Long id) {
        Paciente paciente = pacienteService.obterPacientePorId(id);
        return ResponseEntity.ok(PacienteDTO.fromEntity(paciente));
    }

    /**
     * GET /api/pacientes/cpf/{cpf} - Obter paciente por CPF
     */
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<PacienteDTO> obterPorCpf(@PathVariable String cpf) {
        Paciente paciente = pacienteService.obterPacientePorCpf(cpf);
        return ResponseEntity.ok(PacienteDTO.fromEntity(paciente));
    }

    /**
     * GET /api/pacientes/hospital/{hospitalId} - Listar pacientes por hospital
     */
    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<List<PacienteDTO>> listarPorHospital(@PathVariable Long hospitalId) {
        List<Paciente> pacientes = pacienteService.listarPorHospital(hospitalId);
        List<PacienteDTO> dtos = pacientes.stream()
            .map(PacienteDTO::fromEntity)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /api/pacientes/status/{status} - Listar pacientes por status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<PacienteDTO>> listarPorStatus(@PathVariable String status) {
        List<Paciente> pacientes = pacienteService.listarPorStatus(status);
        List<PacienteDTO> dtos = pacientes.stream()
            .map(PacienteDTO::fromEntity)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /api/pacientes/hospital/{hospitalId}/status/{status} - Listar por hospital e status
     */
    @GetMapping("/hospital/{hospitalId}/status/{status}")
    public ResponseEntity<List<PacienteDTO>> listarPorHospitalEStatus(
            @PathVariable Long hospitalId,
            @PathVariable String status) {
        List<Paciente> pacientes = pacienteService.listarPorHospitalEStatus(hospitalId, status);
        List<PacienteDTO> dtos = pacientes.stream()
            .map(PacienteDTO::fromEntity)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
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
    public ResponseEntity<List<PacienteDTO>> listarPacientesEmProtocoloMEPorHospital(@PathVariable Long hospitalId) {
        List<Paciente> pacientes = pacienteService.listarPacientesEmProtocoloMEPorHospital(hospitalId);
        List<PacienteDTO> dtos = pacientes.stream()
            .map(PacienteDTO::fromEntity)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /api/pacientes/{id}/relatorio-final - Relatório final completo de um paciente
     */
    @GetMapping("/{id:\\d+}/relatorio-final")
    public ResponseEntity<PacienteService.RelatorioFinalPaciente> obterRelatorioFinalPaciente(@PathVariable Long id) {
        PacienteService.RelatorioFinalPaciente relatorio = pacienteService.gerarRelatorioFinalPaciente(id);
        return ResponseEntity.ok(relatorio);
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
    public ResponseEntity<List<PacienteDTO>> buscarPorNome(@RequestParam String nome) {
        List<Paciente> pacientes = pacienteService.procurarPorNome(nome);
        List<PacienteDTO> dtos = pacientes.stream()
            .map(PacienteDTO::fromEntity)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /api/pacientes/hospital/{hospitalId}/buscar?nome={nome} - Procurar por nome no hospital
     */
    @GetMapping("/hospital/{hospitalId}/buscar")
    public ResponseEntity<List<PacienteDTO>> buscarPorNomeEHospital(
            @PathVariable Long hospitalId,
            @RequestParam String nome) {
        List<Paciente> pacientes = pacienteService.procurarPorNomeEHospital(hospitalId, nome);
        List<PacienteDTO> dtos = pacientes.stream()
            .map(PacienteDTO::fromEntity)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * PUT /api/pacientes/{id} - Atualizar paciente
     */
    @Transactional
    @PutMapping("/{id:\\d+}")
    public ResponseEntity<PacienteDTO> atualizar(@PathVariable Long id, @RequestBody Paciente paciente) {
        Paciente pacienteAtualizado = pacienteService.atualizarPaciente(id, paciente);
        return ResponseEntity.ok(PacienteDTO.fromEntity(pacienteAtualizado));
    }

    /**
     * PATCH /api/pacientes/{id}/status - Atualizar status do paciente
     */
    @Transactional
    @PatchMapping("/{id:\\d+}/status")
    public ResponseEntity<PacienteDTO> atualizarStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String statusStr = body.get("status");
        Paciente pacienteAtualizado = pacienteService.atualizarStatus(id, statusStr);
        return ResponseEntity.ok(PacienteDTO.fromEntity(pacienteAtualizado));
    }

    /**
     * DELETE /api/pacientes/{id} - Deletar paciente
     */
    @Transactional
    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        pacienteService.deletarPaciente(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/pacientes/estatisticas - Obter estatísticas de pacientes
     */
    @GetMapping("/estatisticas/resumo")
    public ResponseEntity<PacienteService.PacienteStatisticas> obterEstatisticas() {
        PacienteService.PacienteStatisticas stats = pacienteService.obterEstatisticas();
        return ResponseEntity.ok(stats);
    }

}
