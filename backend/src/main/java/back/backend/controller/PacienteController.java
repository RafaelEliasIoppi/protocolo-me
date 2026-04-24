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

    @Transactional
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Paciente paciente) {
        try {
            Paciente criado = pacienteService.criarPaciente(paciente);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(PacienteDTO.fromEntity(criado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<PacienteDTO>> listarTodos() {
        return ResponseEntity.ok(
                pacienteService.listarTodos()
                        .stream()
                        .map(PacienteDTO::fromEntity)
                        .toList()
        );
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<?> obterPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(
                    PacienteDTO.fromEntity(pacienteService.obterPacientePorId(id))
            );
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<?> obterPorCpf(@PathVariable String cpf) {
        try {
            return ResponseEntity.ok(
                    PacienteDTO.fromEntity(pacienteService.obterPacientePorCpf(cpf))
            );
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<List<PacienteDTO>> listarPorHospital(@PathVariable Long hospitalId) {
        return ResponseEntity.ok(
                pacienteService.listarPorHospital(hospitalId)
                        .stream()
                        .map(PacienteDTO::fromEntity)
                        .toList()
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> listarPorStatus(@PathVariable String status) {
        try {
            return ResponseEntity.ok(
                    pacienteService.listarPorStatus(status)
                            .stream()
                            .map(PacienteDTO::fromEntity)
                            .toList()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Status inválido");
        }
    }

    @GetMapping("/hospital/{hospitalId}/status/{status}")
    public ResponseEntity<?> listarPorHospitalEStatus(
            @PathVariable Long hospitalId,
            @PathVariable String status) {

        try {
            return ResponseEntity.ok(
                    pacienteService.listarPorHospitalEStatus(hospitalId, status)
                            .stream()
                            .map(PacienteDTO::fromEntity)
                            .toList()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Status inválido");
        }
    }

    @Transactional
    @PutMapping("/{id:\\d+}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Paciente paciente) {
        try {
            return ResponseEntity.ok(
                    PacienteDTO.fromEntity(pacienteService.atualizarPaciente(id, paciente))
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Transactional
    @PatchMapping("/{id:\\d+}/status")
    public ResponseEntity<?> atualizarStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        try {
            String status = body.get("status");
            return ResponseEntity.ok(
                    PacienteDTO.fromEntity(pacienteService.atualizarStatus(id, status))
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Status inválido");
        }
    }

    @Transactional
    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        pacienteService.deletarPaciente(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/estatisticas/resumo")
    public ResponseEntity<PacienteService.PacienteStatisticas> obterEstatisticas() {
        return ResponseEntity.ok(pacienteService.obterEstatisticas());
    }
}