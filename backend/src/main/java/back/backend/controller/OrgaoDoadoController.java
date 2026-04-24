package back.backend.controller;

import back.backend.dto.OrgaoDoadoDTO;
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

    public OrgaoDoadoController(OrgaoDoadoService orgaoDoadoService) {
        this.orgaoDoadoService = orgaoDoadoService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<OrgaoDoadoDTO> criar(@RequestBody back.backend.model.OrgaoDoado orgaoDoado) {

        var novo = orgaoDoadoService.criar(orgaoDoado);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(OrgaoDoadoDTO.fromEntity(novo));
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<OrgaoDoadoDTO> buscarPorId(@PathVariable Long id) {

        var orgao = orgaoDoadoService.buscarPorId(id);

        return ResponseEntity.ok(OrgaoDoadoDTO.fromEntity(orgao));
    }

    // LIST BY PROTOCOLO
    @GetMapping("/protocolo/{protocoloId}")
    public ResponseEntity<List<OrgaoDoadoDTO>> listarPorProtocolo(@PathVariable Long protocoloId) {

        List<OrgaoDoadoDTO> lista = orgaoDoadoService.listarPorProtocolo(protocoloId)
                .stream()
                .map(OrgaoDoadoDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(lista);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<OrgaoDoadoDTO> atualizar(@PathVariable Long id,
                                                   @RequestBody back.backend.model.OrgaoDoado orgaoDoado) {

        var atualizado = orgaoDoadoService.atualizar(id, orgaoDoado);

        return ResponseEntity.ok(OrgaoDoadoDTO.fromEntity(atualizado));
    }

    // IMPLANTAR
    @PostMapping("/{id}/implantar")
    public ResponseEntity<OrgaoDoadoDTO> registrarImplantacao(
            @PathVariable Long id,
            @RequestParam String hospitalReceptor,
            @RequestParam String pacienteReceptor) {

        var implantado =
                orgaoDoadoService.registrarImplantacao(id, hospitalReceptor, pacienteReceptor);

        return ResponseEntity.ok(OrgaoDoadoDTO.fromEntity(implantado));
    }

    // DESCARTAR
    @PostMapping("/{id}/descartar")
    public ResponseEntity<OrgaoDoadoDTO> registrarDescarte(
            @PathVariable Long id,
            @RequestParam String motivo) {

        var descartado =
                orgaoDoadoService.registrarDescarte(id, motivo);

        return ResponseEntity.ok(OrgaoDoadoDTO.fromEntity(descartado));
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