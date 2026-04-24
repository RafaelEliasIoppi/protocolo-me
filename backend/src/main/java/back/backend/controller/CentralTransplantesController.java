package back.backend.controller;

import back.backend.dto.AcaoResponseDTO;
import back.backend.dto.CentralTransplantesDTO;
import back.backend.model.CentralTransplantes;
import back.backend.service.CentralTransplantesService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/centrais-transplantes")
public class CentralTransplantesController {

    private final CentralTransplantesService centralService;

    public CentralTransplantesController(CentralTransplantesService centralService) {
        this.centralService = centralService;
    }

    // ---------------- CREATE ----------------

    @PostMapping
    public ResponseEntity<CentralTransplantesDTO> criar(@Valid @RequestBody CentralTransplantesDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toDTO(centralService.criarCentralFromDTO(dto)));
    }

    // ---------------- READ ALL ----------------

    @GetMapping
    public ResponseEntity<List<CentralTransplantesDTO>> listarTodas() {
        return ResponseEntity.ok(
                centralService.listarTodas()
                        .stream()
                        .map(this::toDTO)
                        .collect(Collectors.toList())
        );
    }

    // ---------------- READ BY ID ----------------

    @GetMapping("/{id}")
    public ResponseEntity<CentralTransplantesDTO> buscarPorId(@PathVariable Long id) {

        Optional<CentralTransplantes> central = centralService.buscarPorId(id);

        return central.map(c -> ResponseEntity.ok(toDTO(c)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ---------------- FILTERS ----------------

    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<CentralTransplantesDTO> buscarPorCnpj(@PathVariable String cnpj) {

        return centralService.buscarPorCnpj(cnpj)
                .map(c -> ResponseEntity.ok(toDTO(c)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<CentralTransplantesDTO> buscarPorNome(@PathVariable String nome) {

        return centralService.buscarPorNome(nome)
                .map(c -> ResponseEntity.ok(toDTO(c)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/cidade/{cidade}")
    public ResponseEntity<List<CentralTransplantesDTO>> listarPorCidade(@PathVariable String cidade) {
        return ResponseEntity.ok(mapList(centralService.listarPorCidade(cidade)));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<CentralTransplantesDTO>> listarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(mapList(centralService.listarPorEstado(estado)));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<CentralTransplantesDTO>> listarPorStatus(@PathVariable String status) {

        CentralTransplantes.StatusCentral statusEnum;

        try {
            statusEnum = CentralTransplantes.StatusCentral.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(
                mapList(centralService.listarPorStatus(statusEnum))
        );
    }

    // ---------------- UPDATE ----------------

    @PutMapping("/{id}")
    public ResponseEntity<CentralTransplantesDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody CentralTransplantesDTO dto) {

        return ResponseEntity.ok(
                toDTO(centralService.atualizarCentralFromDTO(id, dto))
        );
    }

    // ---------------- PATCH STATUS ----------------

    @PatchMapping("/{id}/status")
    public ResponseEntity<CentralTransplantesDTO> alterarStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        CentralTransplantes.StatusCentral novoStatus;

        try {
            novoStatus = CentralTransplantes.StatusCentral.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(
                toDTO(centralService.alterarStatus(id, novoStatus))
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

    // ---------------- HELPERS ----------------

    private CentralTransplantesDTO toDTO(CentralTransplantes c) {
        CentralTransplantesDTO dto = new CentralTransplantesDTO();
        dto.setId(c.getId());
        dto.setNome(c.getNome());
        dto.setCnpj(c.getCnpj());
        dto.setEndereco(c.getEndereco());
        dto.setCidade(c.getCidade());
        dto.setEstado(c.getEstado());
        dto.setTelefone(c.getTelefone());
        dto.setTelefonePlantao(c.getTelefonePlantao());
        dto.setEmail(c.getEmail());
        dto.setEmailPlantao(c.getEmailPlantao());
        dto.setCoordenador(c.getCoordenador());
        dto.setTelefoneCoordenador(c.getTelefoneCoordenador());
        dto.setCapacidadeProcessamento(c.getCapacidadeProcessamento());
        dto.setEspecialidadesOrgaos(c.getEspecialidadesOrgaos());
        return dto;
    }

    private List<CentralTransplantesDTO> mapList(List<CentralTransplantes> list) {
        return list.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}