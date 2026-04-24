package back.backend.controller;

import back.backend.dto.CentralTransplantesDTO;
import back.backend.dto.CentralTransplantesRequestDTO;
import back.backend.model.CentralTransplantes;
import back.backend.service.CentralTransplantesService;

import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/centrais-transplantes")
@lombok.RequiredArgsConstructor
public class CentralTransplantesController {

    private final CentralTransplantesService centralService;

    // ---------------- CREATE ----------------

    @PostMapping
    public ResponseEntity<CentralTransplantesDTO> criar(@Valid @RequestBody CentralTransplantesRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(centralService.criarCentralFromDTO(dto));
    }

    // ---------------- READ ALL ----------------

    @GetMapping
    public ResponseEntity<List<CentralTransplantesDTO>> listarTodas() {
        return ResponseEntity.ok(
            centralService.listarTodas()
        );
    }

    // ---------------- READ BY ID ----------------

    @GetMapping("/{id}")
    public ResponseEntity<CentralTransplantesDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(centralService.buscarPorIdOuFalhar(id));
    }

    // ---------------- FILTERS ----------------

    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<CentralTransplantesDTO> buscarPorCnpj(@PathVariable String cnpj) {
        return ResponseEntity.ok(centralService.buscarPorCnpjOuFalhar(cnpj));
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<CentralTransplantesDTO> buscarPorNome(@PathVariable String nome) {
        return ResponseEntity.ok(centralService.buscarPorNomeOuFalhar(nome));
    }

    @GetMapping("/cidade/{cidade}")
    public ResponseEntity<List<CentralTransplantesDTO>> listarPorCidade(@PathVariable String cidade) {
        return ResponseEntity.ok(
                centralService.listarPorCidade(cidade)
        );
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<CentralTransplantesDTO>> listarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(
                centralService.listarPorEstado(estado)
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<CentralTransplantesDTO>> listarPorStatus(@PathVariable String status) {
        return ResponseEntity.ok(centralService.listarPorStatus(status));
    }

    // ---------------- UPDATE ----------------

    @PutMapping("/{id}")
    public ResponseEntity<CentralTransplantesDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody CentralTransplantesRequestDTO dto) {

        return ResponseEntity.ok(centralService.atualizarCentralFromDTO(id, dto));
    }

    // ---------------- PATCH STATUS ----------------

    @PatchMapping("/{id}/status")
    public ResponseEntity<CentralTransplantesDTO> alterarStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(centralService.alterarStatus(id, status));
    }

    // ---------------- RELATIONSHIPS ----------------

    @PostMapping("/{centralId}/hospitais/{hospitalId}")
    public ResponseEntity<CentralTransplantesDTO> vincularHospital(
            @PathVariable Long centralId,
            @PathVariable Long hospitalId) {

        return ResponseEntity.ok(centralService.vincularHospital(centralId, hospitalId));
    }

    @DeleteMapping("/{centralId}/hospitais/{hospitalId}")
    public ResponseEntity<CentralTransplantesDTO> removerHospital(
            @PathVariable Long centralId,
            @PathVariable Long hospitalId) {

        return ResponseEntity.ok(centralService.removerHospital(centralId, hospitalId));
    }

    // ---------------- DELETE ----------------

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        centralService.deletarCentral(id);
        return ResponseEntity.noContent().build();
    }

    // ---------------- STATS ----------------

    @GetMapping("/estatisticas/doadores-receptores")
    public ResponseEntity<CentralTransplantesService.EstatisticasCentralDoacaoTransplante> estatisticas() {
        return ResponseEntity.ok(centralService.obterEstatisticasDoacaoTransplante());
    }
}