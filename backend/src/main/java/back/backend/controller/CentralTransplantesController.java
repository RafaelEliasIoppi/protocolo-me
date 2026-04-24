package back.backend.controller;

import back.backend.dto.AcaoResponseDTO;
import back.backend.dto.CentralTransplantesDTO;
import back.backend.model.CentralTransplantes;
import back.backend.mapper.CentralTransplantesMapper;
import back.backend.service.CentralTransplantesService;

import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/centrais-transplantes")
@lombok.RequiredArgsConstructor
public class CentralTransplantesController {

    private final CentralTransplantesService centralService;
    private final CentralTransplantesMapper centralTransplantesMapper;

    // ---------------- CREATE ----------------

    @PostMapping
    public ResponseEntity<CentralTransplantesDTO> criar(@Valid @RequestBody CentralTransplantesDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(centralTransplantesMapper.toDTO(centralService.criarCentralFromDTO(dto)));
    }

    // ---------------- READ ALL ----------------

    @GetMapping
    public ResponseEntity<List<CentralTransplantesDTO>> listarTodas() {
        return ResponseEntity.ok(
                centralService.listarTodas()
                        .stream()
                .map(centralTransplantesMapper::toDTO)
                        .collect(Collectors.toList())
        );
    }

    // ---------------- READ BY ID ----------------

    @GetMapping("/{id}")
    public ResponseEntity<CentralTransplantesDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(centralTransplantesMapper.toDTO(centralService.buscarPorIdOuFalhar(id)));
    }

    // ---------------- FILTERS ----------------

    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<CentralTransplantesDTO> buscarPorCnpj(@PathVariable String cnpj) {
        return ResponseEntity.ok(centralTransplantesMapper.toDTO(centralService.buscarPorCnpjOuFalhar(cnpj)));
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<CentralTransplantesDTO> buscarPorNome(@PathVariable String nome) {
        return ResponseEntity.ok(centralTransplantesMapper.toDTO(centralService.buscarPorNomeOuFalhar(nome)));
    }

    @GetMapping("/cidade/{cidade}")
    public ResponseEntity<List<CentralTransplantesDTO>> listarPorCidade(@PathVariable String cidade) {
        return ResponseEntity.ok(
                centralService.listarPorCidade(cidade)
                        .stream()
                        .map(centralTransplantesMapper::toDTO)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<CentralTransplantesDTO>> listarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(
                centralService.listarPorEstado(estado)
                        .stream()
                        .map(centralTransplantesMapper::toDTO)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<CentralTransplantesDTO>> listarPorStatus(@PathVariable String status) {
        CentralTransplantes.StatusCentral statusEnum = CentralTransplantes.StatusCentral.valueOf(status.toUpperCase());

        return ResponseEntity.ok(
                centralService.listarPorStatus(statusEnum)
                        .stream()
                        .map(centralTransplantesMapper::toDTO)
                        .collect(Collectors.toList())
        );
    }

    // ---------------- UPDATE ----------------

    @PutMapping("/{id}")
    public ResponseEntity<CentralTransplantesDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody CentralTransplantesDTO dto) {

        return ResponseEntity.ok(
            centralTransplantesMapper.toDTO(centralService.atualizarCentralFromDTO(id, dto))
        );
    }

    // ---------------- PATCH STATUS ----------------

    @PatchMapping("/{id}/status")
    public ResponseEntity<CentralTransplantesDTO> alterarStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        CentralTransplantes.StatusCentral novoStatus = CentralTransplantes.StatusCentral.valueOf(status.toUpperCase());

        return ResponseEntity.ok(
            centralTransplantesMapper.toDTO(centralService.alterarStatus(id, novoStatus))
        );
    }

    // ---------------- RELATIONSHIPS ----------------

    @PostMapping("/{centralId}/hospitais/{hospitalId}")
    public ResponseEntity<AcaoResponseDTO> vincularHospital(
            @PathVariable Long centralId,
            @PathVariable Long hospitalId) {

        centralService.vincularHospital(centralId, hospitalId);

        return ResponseEntity.ok(
                new AcaoResponseDTO(centralId, "Hospital vinculado com sucesso")
        );
    }

    @DeleteMapping("/{centralId}/hospitais/{hospitalId}")
    public ResponseEntity<AcaoResponseDTO> removerHospital(
            @PathVariable Long centralId,
            @PathVariable Long hospitalId) {

        centralService.removerHospital(centralId, hospitalId);

        return ResponseEntity.ok(
                new AcaoResponseDTO(centralId, "Hospital removido com sucesso")
        );
    }

    // ---------------- DELETE ----------------

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        centralService.deletarCentral(id);
        return ResponseEntity.noContent().build();
    }

    // ---------------- STATS ----------------

    @GetMapping("/estatisticas/doadores-receptores")
    public ResponseEntity<?> estatisticas() {
        return ResponseEntity.ok(
                centralService.obterEstatisticasDoacaoTransplante()
        );
    }
}