package back.backend.controller;

import back.backend.model.CentralTransplantes;
import back.backend.service.CentralTransplantesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/centrais-transplantes")
@CrossOrigin(origins = "*")
public class CentralTransplantesController {

    @Autowired
    private CentralTransplantesService centralService;

    // POST - Criar nova central
    @PostMapping
    public ResponseEntity<CentralTransplantes> criarCentral(@RequestBody CentralTransplantes central) {
        try {
            CentralTransplantes novaCentral = centralService.criarCentral(central);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaCentral);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // GET - Listar todas as centrais
    @GetMapping
    public ResponseEntity<List<CentralTransplantes>> listarTodas() {
        List<CentralTransplantes> centrais = centralService.listarTodas();
        return ResponseEntity.ok(centrais);
    }

    // GET - Buscar central por ID
    @GetMapping("/{id}")
    public ResponseEntity<CentralTransplantes> buscarPorId(@PathVariable Long id) {
        Optional<CentralTransplantes> central = centralService.buscarPorId(id);
        return central.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET - Buscar por CNPJ
    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<CentralTransplantes> buscarPorCnpj(@PathVariable String cnpj) {
        Optional<CentralTransplantes> central = centralService.buscarPorCnpj(cnpj);
        return central.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET - Buscar por Nome
    @GetMapping("/nome/{nome}")
    public ResponseEntity<CentralTransplantes> buscarPorNome(@PathVariable String nome) {
        Optional<CentralTransplantes> central = centralService.buscarPorNome(nome);
        return central.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET - Listar por cidade
    @GetMapping("/cidade/{cidade}")
    public ResponseEntity<List<CentralTransplantes>> listarPorCidade(@PathVariable String cidade) {
        List<CentralTransplantes> centrais = centralService.listarPorCidade(cidade);
        return ResponseEntity.ok(centrais);
    }

    // GET - Listar por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<CentralTransplantes>> listarPorEstado(@PathVariable String estado) {
        List<CentralTransplantes> centrais = centralService.listarPorEstado(estado);
        return ResponseEntity.ok(centrais);
    }

    // GET - Listar por status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<CentralTransplantes>> listarPorStatus(@PathVariable String status) {
        try {
            CentralTransplantes.StatusCentral statusEnum = CentralTransplantes.StatusCentral.valueOf(status.toUpperCase());
            List<CentralTransplantes> centrais = centralService.listarPorStatus(statusEnum);
            return ResponseEntity.ok(centrais);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // GET - Estatísticas de doadores/receptores e órgãos/tecidos (Painel da Central)
    @GetMapping("/estatisticas/doadores-receptores")
    public ResponseEntity<CentralTransplantesService.EstatisticasCentralDoacaoTransplante> obterEstatisticasDoadoresReceptores() {
        CentralTransplantesService.EstatisticasCentralDoacaoTransplante estatisticas =
                centralService.obterEstatisticasDoacaoTransplante();
        return ResponseEntity.ok(estatisticas);
    }

    // PUT - Atualizar central
    @PutMapping("/{id}")
    public ResponseEntity<CentralTransplantes> atualizarCentral(@PathVariable Long id, @RequestBody CentralTransplantes central) {
        try {
            CentralTransplantes centralAtualizada = centralService.atualizarCentral(id, central);
            return ResponseEntity.ok(centralAtualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // PATCH - Alterar status da central
    @PatchMapping("/{id}/status")
    public ResponseEntity<CentralTransplantes> alterarStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        try {
            CentralTransplantes.StatusCentral novoStatus = CentralTransplantes.StatusCentral.valueOf(status.toUpperCase());
            CentralTransplantes central = centralService.alterarStatus(id, novoStatus);
            return ResponseEntity.ok(central);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST - Vincular hospital à central
    @PostMapping("/{centralId}/hospitais/{hospitalId}")
    public ResponseEntity<CentralTransplantes> vincularHospital(
            @PathVariable Long centralId,
            @PathVariable Long hospitalId) {
        try {
            CentralTransplantes central = centralService.vincularHospital(centralId, hospitalId);
            return ResponseEntity.ok(central);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE - Remover hospital da central
    @DeleteMapping("/{centralId}/hospitais/{hospitalId}")
    public ResponseEntity<CentralTransplantes> removerHospital(
            @PathVariable Long centralId,
            @PathVariable Long hospitalId) {
        try {
            CentralTransplantes central = centralService.removerHospital(centralId, hospitalId);
            return ResponseEntity.ok(central);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE - Deletar central
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCentral(@PathVariable Long id) {
        try {
            centralService.deletarCentral(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
