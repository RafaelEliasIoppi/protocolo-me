package back.backend.controller;

import back.backend.dto.OrgaoDoadoDTO;
import back.backend.dto.OrgaoDoadoRequestDTO;
import back.backend.mapper.OrgaoDoadoRequestMapper;
import back.backend.mapper.OrgaoDoadoMapper;
import javax.validation.Valid;
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
    private final OrgaoDoadoMapper orgaoDoadoMapper;
    private final OrgaoDoadoRequestMapper orgaoDoadoRequestMapper;

    public OrgaoDoadoController(OrgaoDoadoService orgaoDoadoService, OrgaoDoadoMapper orgaoDoadoMapper, OrgaoDoadoRequestMapper orgaoDoadoRequestMapper) {
        this.orgaoDoadoService = orgaoDoadoService;
        this.orgaoDoadoMapper = orgaoDoadoMapper;
        this.orgaoDoadoRequestMapper = orgaoDoadoRequestMapper;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<OrgaoDoadoDTO> criar(@Valid @RequestBody OrgaoDoadoRequestDTO request) {

        var orgaoDoado = orgaoDoadoRequestMapper.toEntity(request);

        var novo = orgaoDoadoService.criar(orgaoDoado);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(orgaoDoadoMapper.toDTO(novo));
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<OrgaoDoadoDTO> buscarPorId(@PathVariable Long id) {

        var orgao = orgaoDoadoService.buscarPorId(id);

        return ResponseEntity.ok(orgaoDoadoMapper.toDTO(orgao));
    }

    // LIST BY PROTOCOLO
    @GetMapping("/protocolo/{protocoloId}")
    public ResponseEntity<List<OrgaoDoadoDTO>> listarPorProtocolo(@PathVariable Long protocoloId) {

        List<OrgaoDoadoDTO> lista = orgaoDoadoService.listarPorProtocolo(protocoloId)
                .stream()
            .map(orgaoDoadoMapper::toDTO)
                .toList();

        return ResponseEntity.ok(lista);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<OrgaoDoadoDTO> atualizar(@PathVariable Long id,
                                                   @Valid @RequestBody OrgaoDoadoRequestDTO request) {

        var orgaoDoado = orgaoDoadoRequestMapper.toEntity(request);

        var atualizado = orgaoDoadoService.atualizar(id, orgaoDoado);

        return ResponseEntity.ok(orgaoDoadoMapper.toDTO(atualizado));
    }

    // IMPLANTAR
    @PostMapping("/{id}/implantar")
    public ResponseEntity<OrgaoDoadoDTO> registrarImplantacao(
            @PathVariable Long id,
            @RequestParam String hospitalReceptor,
            @RequestParam String pacienteReceptor) {

        var implantado =
                orgaoDoadoService.registrarImplantacao(id, hospitalReceptor, pacienteReceptor);

        return ResponseEntity.ok(orgaoDoadoMapper.toDTO(implantado));
    }

    // DESCARTAR
    @PostMapping("/{id}/descartar")
    public ResponseEntity<OrgaoDoadoDTO> registrarDescarte(
            @PathVariable Long id,
            @RequestParam String motivo) {

        var descartado =
                orgaoDoadoService.registrarDescarte(id, motivo);

        return ResponseEntity.ok(orgaoDoadoMapper.toDTO(descartado));
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