package back.backend.controller;

import back.backend.dto.OrgaoDoadoDTO;
import back.backend.dto.OrgaoDoadoRequestDTO;
import back.backend.mapper.OrgaoDoadoRequestMapper;
import jakarta.validation.Valid;
import back.backend.service.OrgaoDoadoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orgaos-doados")
@CrossOrigin(origins = "*")
public class OrgaoDoadoController {

    private final OrgaoDoadoService orgaoDoadoService;
    private final OrgaoDoadoRequestMapper orgaoDoadoRequestMapper;

    public OrgaoDoadoController(OrgaoDoadoService orgaoDoadoService, OrgaoDoadoRequestMapper orgaoDoadoRequestMapper) {
        this.orgaoDoadoService = orgaoDoadoService;
        this.orgaoDoadoRequestMapper = orgaoDoadoRequestMapper;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<OrgaoDoadoDTO> criar(@Valid @RequestBody OrgaoDoadoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orgaoDoadoService.criar(orgaoDoadoRequestMapper.toEntity(request)));
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<OrgaoDoadoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(orgaoDoadoService.buscarPorId(id));
    }

    // LIST BY PROTOCOLO
    @GetMapping("/protocolo/{protocoloId}")
    public ResponseEntity<List<OrgaoDoadoDTO>> listarPorProtocolo(@PathVariable Long protocoloId) {
        return ResponseEntity.ok(orgaoDoadoService.listarPorProtocolo(protocoloId));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<OrgaoDoadoDTO> atualizar(@PathVariable Long id,
                                                   @Valid @RequestBody OrgaoDoadoRequestDTO request) {
        return ResponseEntity.ok(orgaoDoadoService.atualizar(id, orgaoDoadoRequestMapper.toEntity(request)));
    }

    // IMPLANTAR
    @PostMapping("/{id}/implantar")
    public ResponseEntity<OrgaoDoadoDTO> registrarImplantacao(
            @PathVariable Long id,
            @RequestParam String hospitalReceptor,
            @RequestParam String pacienteReceptor) {

        return ResponseEntity.ok(orgaoDoadoService.registrarImplantacao(id, hospitalReceptor, pacienteReceptor));
    }

    // DESCARTAR
    @PostMapping("/{id}/descartar")
    public ResponseEntity<OrgaoDoadoDTO> registrarDescarte(
            @PathVariable Long id,
            @RequestParam String motivo) {

        return ResponseEntity.ok(orgaoDoadoService.registrarDescarte(id, motivo));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {

        orgaoDoadoService.deletar(id);

        return ResponseEntity.noContent().build();
    }

    // STATS
    @GetMapping("/protocolo/{protocoloId}/estatisticas")
    public ResponseEntity<OrgaoDoadoService.OrgaoStatisticas> obterEstatisticas(
            @PathVariable Long protocoloId) {

        return ResponseEntity.ok(
                orgaoDoadoService.obterEstatisticas(protocoloId)
        );
    }
}