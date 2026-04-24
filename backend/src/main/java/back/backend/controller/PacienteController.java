package back.backend.controller;

import back.backend.dto.PacienteRequestDTO;
import back.backend.dto.PacienteStatusRequestDTO;
import back.backend.mapper.PacienteRequestMapper;
import back.backend.dto.PacienteDTO;
import back.backend.service.PacienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import javax.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pacientes")
@CrossOrigin(origins = "*")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PacienteController {

    private final PacienteService pacienteService;
    private final PacienteRequestMapper pacienteRequestMapper;

    @Transactional
    @PostMapping
    public ResponseEntity<PacienteDTO> criar(@Valid @RequestBody PacienteRequestDTO request) {
        return ResponseEntity.status(201).body(pacienteService.criarPaciente(pacienteRequestMapper.toEntity(request)));
    }

    @GetMapping
    public ResponseEntity<List<PacienteDTO>> listarTodos() {
        return ResponseEntity.ok(pacienteService.listarTodos());
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<PacienteDTO> obterPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pacienteService.obterPacientePorId(id));
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<PacienteDTO> obterPorCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(pacienteService.obterPacientePorCpf(cpf));
    }

    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<List<PacienteDTO>> listarPorHospital(@PathVariable Long hospitalId) {
        return ResponseEntity.ok(pacienteService.listarPorHospital(hospitalId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PacienteDTO>> listarPorStatus(@PathVariable String status) {
        return ResponseEntity.ok(pacienteService.listarPorStatus(status));
    }

    @GetMapping("/hospital/{hospitalId}/status/{status}")
    public ResponseEntity<?> listarPorHospitalEStatus(
            @PathVariable Long hospitalId,
            @PathVariable String status) {

        return ResponseEntity.ok(pacienteService.listarPorHospitalEStatus(hospitalId, status));
    }

    @Transactional
    @PutMapping("/{id:\\d+}")
    public ResponseEntity<PacienteDTO> atualizar(@PathVariable Long id, @Valid @RequestBody PacienteRequestDTO request) {
        return ResponseEntity.ok(pacienteService.atualizarPaciente(id, pacienteRequestMapper.toEntity(request)));
    }

    @Transactional
    @PatchMapping("/{id:\\d+}/status")
    public ResponseEntity<PacienteDTO> atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody PacienteStatusRequestDTO request) {

        return ResponseEntity.ok(pacienteService.atualizarStatus(id, request.getStatus()));
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