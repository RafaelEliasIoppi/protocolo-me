package back.backend.controller;

import back.backend.dto.ErrorResponseDTO;
import back.backend.dto.OrgaoDoadoDTO;
import back.backend.model.OrgaoDoado;
import back.backend.service.OrgaoDoadoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orgaos-doados")
@CrossOrigin(origins = "*")
public class OrgaoDoadoController {

    private final OrgaoDoadoService orgaoDoadoService;

    public OrgaoDoadoController(OrgaoDoadoService orgaoDoadoService) {
        this.orgaoDoadoService = orgaoDoadoService;
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody OrgaoDoado orgaoDoado) {
        try {
            OrgaoDoado novo = orgaoDoadoService.criar(orgaoDoado);
            return ResponseEntity.status(201).body(OrgaoDoadoDTO.fromEntity(novo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponseDTO(e.getMessage(), 400));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<OrgaoDoado> orgao = orgaoDoadoService.buscarPorId(id);

        return orgao.map(o -> ResponseEntity.ok(OrgaoDoadoDTO.fromEntity(o)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/protocolo/{protocoloId}")
    public ResponseEntity<List<OrgaoDoadoDTO>> listarPorProtocolo(@PathVariable Long protocoloId) {
        return ResponseEntity.ok(
                orgaoDoadoService.listarPorProtocolo(protocoloId)
                        .stream()
                        .map(OrgaoDoadoDTO::fromEntity)
                        .toList()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id,
                                       @RequestBody OrgaoDoado orgaoDoado) {
        try {
            OrgaoDoado atualizado = orgaoDoadoService.atualizar(id, orgaoDoado);
            return ResponseEntity.ok(OrgaoDoadoDTO.fromEntity(atualizado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponseDTO(e.getMessage(), 400));
        }
    }

    @PostMapping("/{id}/implantar")
    public ResponseEntity<?> registrarImplantacao(
            @PathVariable Long id,
            @RequestParam String hospitalReceptor,
            @RequestParam String pacienteReceptor) {

        OrgaoDoado implantado =
                orgaoDoadoService.registrarImplantacao(id, hospitalReceptor, pacienteReceptor);

        return ResponseEntity.ok(OrgaoDoadoDTO.fromEntity(implantado));
    }

    @PostMapping("/{id}/descartar")
    public ResponseEntity<?> registrarDescarte(
            @PathVariable Long id,
            @RequestParam String motivo) {

        OrgaoDoado descartado =
                orgaoDoadoService.registrarDescarte(id, motivo);

        return ResponseEntity.ok(OrgaoDoadoDTO.fromEntity(descartado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        orgaoDoadoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/protocolo/{protocoloId}/estatisticas")
    public ResponseEntity<?> obterEstatisticas(@PathVariable Long protocoloId) {
        return ResponseEntity.ok(
                orgaoDoadoService.obterEstatisticas(protocoloId)
        );
    }
}