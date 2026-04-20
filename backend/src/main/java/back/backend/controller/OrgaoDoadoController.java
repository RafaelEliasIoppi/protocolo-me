package back.backend.controller;

import back.backend.model.OrgaoDoado;
import back.backend.service.OrgaoDoadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/orgaos-doados")
@CrossOrigin(origins = "*")
public class OrgaoDoadoController {

    @Autowired
    private OrgaoDoadoService orgaoDoadoService;

    /**
     * POST - Criar novo órgão doado
     */
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody OrgaoDoado orgaoDoado) {
        try {
            OrgaoDoado novo = orgaoDoadoService.criar(orgaoDoado);
            return ResponseEntity.status(201).body(novo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    /**
     * GET - Buscar órgão doado por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrgaoDoado> buscarPorId(@PathVariable Long id) {
        Optional<OrgaoDoado> orgaoDoado = orgaoDoadoService.buscarPorId(id);
        return orgaoDoado.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * GET - Listar órgãos doados por protocolo
     */
    @GetMapping("/protocolo/{protocoloId}")
    public ResponseEntity<List<OrgaoDoado>> listarPorProtocolo(@PathVariable Long protocoloId) {
        List<OrgaoDoado> orgaos = orgaoDoadoService.listarPorProtocolo(protocoloId);
        return ResponseEntity.ok(orgaos);
    }

    /**
     * PUT - Atualizar órgão doado
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody OrgaoDoado orgaoDoadoAtualizado) {
        try {
            OrgaoDoado atualizado = orgaoDoadoService.atualizar(id, orgaoDoadoAtualizado);
            return ResponseEntity.ok(atualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST - Registrar implantação
     */
    @PostMapping("/{id}/implantar")
    public ResponseEntity<OrgaoDoado> registrarImplantacao(
            @PathVariable Long id,
            @RequestParam String hospitalReceptor,
            @RequestParam String pacienteReceptor) {
        try {
            OrgaoDoado implantado = orgaoDoadoService.registrarImplantacao(id, hospitalReceptor, pacienteReceptor);
            return ResponseEntity.ok(implantado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST - Registrar descarte
     */
    @PostMapping("/{id}/descartar")
    public ResponseEntity<OrgaoDoado> registrarDescarte(
            @PathVariable Long id,
            @RequestParam String motivo) {
        try {
            OrgaoDoado descartado = orgaoDoadoService.registrarDescarte(id, motivo);
            return ResponseEntity.ok(descartado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE - Deletar órgão doado
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            orgaoDoadoService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET - Obter estatísticas de órgãos de um protocolo
     */
    @GetMapping("/protocolo/{protocoloId}/estatisticas")
    public ResponseEntity<OrgaoDoadoService.OrgaoStatisticas> obterEstatisticas(@PathVariable Long protocoloId) {
        OrgaoDoadoService.OrgaoStatisticas stats = orgaoDoadoService.obterEstatisticas(protocoloId);
        return ResponseEntity.ok(stats);
    }
}
