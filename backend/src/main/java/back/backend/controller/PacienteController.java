package back.backend.controller;

import back.backend.dto.PacienteRequestDTO;
import back.backend.dto.PacienteStatusRequestDTO;
import back.backend.mapper.PacienteMapper;
import back.backend.mapper.PacienteRequestMapper;
import back.backend.model.Paciente;
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
    private final PacienteMapper pacienteMapper;
    private final PacienteRequestMapper pacienteRequestMapper;

    @Transactional
    @PostMapping
    public ResponseEntity<PacienteDTO> criar(@Valid @RequestBody PacienteRequestDTO request) {
        Paciente paciente = pacienteRequestMapper.toEntity(request);
        Paciente criado = pacienteService.criarPaciente(paciente);
        return ResponseEntity.status(201).body(pacienteMapper.toDTO(criado));
    }

    @GetMapping
    public ResponseEntity<List<PacienteDTO>> listarTodos() {
        return ResponseEntity.ok(
                pacienteService.listarTodos()
                        .stream()
                .map(pacienteMapper::toDTO)
                        .toList()
        );
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<PacienteDTO> obterPorId(@PathVariable Long id) {
        return ResponseEntity.ok(
                pacienteMapper.toDTO(pacienteService.obterPacientePorId(id))
        );
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<PacienteDTO> obterPorCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(
                pacienteMapper.toDTO(pacienteService.obterPacientePorCpf(cpf))
        );
    }

    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<List<PacienteDTO>> listarPorHospital(@PathVariable Long hospitalId) {
        return ResponseEntity.ok(
                pacienteService.listarPorHospital(hospitalId)
                        .stream()
                .map(pacienteMapper::toDTO)
                        .toList()
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PacienteDTO>> listarPorStatus(@PathVariable String status) {
        return ResponseEntity.ok(
                pacienteService.listarPorStatus(status)
                        .stream()
                        .map(pacienteMapper::toDTO)
                        .toList()
        );
    }

    @GetMapping("/hospital/{hospitalId}/status/{status}")
    public ResponseEntity<?> listarPorHospitalEStatus(
            @PathVariable Long hospitalId,
            @PathVariable String status) {

        return ResponseEntity.ok(
                pacienteService.listarPorHospitalEStatus(hospitalId, status)
                        .stream()
                        .map(pacienteMapper::toDTO)
                        .toList()
        );
    }

    @Transactional
    @PutMapping("/{id:\\d+}")
    public ResponseEntity<PacienteDTO> atualizar(@PathVariable Long id, @Valid @RequestBody PacienteRequestDTO request) {
        Paciente paciente = pacienteRequestMapper.toEntity(request);
        return ResponseEntity.ok(pacienteMapper.toDTO(pacienteService.atualizarPaciente(id, paciente)));
    }

    @Transactional
    @PatchMapping("/{id:\\d+}/status")
    public ResponseEntity<PacienteDTO> atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody PacienteStatusRequestDTO request) {

        return ResponseEntity.ok(pacienteMapper.toDTO(pacienteService.atualizarStatus(id, request.getStatus())));
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