package back.backend.controller;

import back.backend.model.ExameME;
import back.backend.service.ExameMEService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/exames-me")
@CrossOrigin(origins = "*")
public class ExameMEController {

    @Autowired
    private ExameMEService exameService;

    // POST - Criar novo exame
    @PostMapping
    public ResponseEntity<ExameME> criarExame(@RequestBody ExameME exame) {
        try {
            ExameME novoExame = exameService.criarExame(exame);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoExame);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // GET - Listar exames de um protocolo
    @GetMapping("/protocolo/{protocoloId}")
    public ResponseEntity<List<ExameME>> listarExamePorProtocolo(@PathVariable Long protocoloId) {
        try {
            List<ExameME> exames = exameService.listarExamesPorProtocolo(protocoloId);
            return ResponseEntity.ok(exames);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET - Listar exames clínicos
    @GetMapping("/protocolo/{protocoloId}/clinicos")
    public ResponseEntity<List<ExameME>> listarExamesClinico(@PathVariable Long protocoloId) {
        try {
            List<ExameME> exames = exameService.listarExamesClinico(protocoloId);
            return ResponseEntity.ok(exames);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET - Listar exames complementares
    @GetMapping("/protocolo/{protocoloId}/complementares")
    public ResponseEntity<List<ExameME>> listarExamesComplementares(@PathVariable Long protocoloId) {
        try {
            List<ExameME> exames = exameService.listarExamesComplementares(protocoloId);
            return ResponseEntity.ok(exames);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET - Listar exames laboratoriais
    @GetMapping("/protocolo/{protocoloId}/laboratoriais")
    public ResponseEntity<List<ExameME>> listarExamesLaboratoriais(@PathVariable Long protocoloId) {
        try {
            List<ExameME> exames = exameService.listarExamesLaboratoriais(protocoloId);
            return ResponseEntity.ok(exames);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET - Buscar exame por ID
    @GetMapping("/{id}")
    public ResponseEntity<ExameME> buscarPorId(@PathVariable Long id) {
        Optional<ExameME> exame = exameService.buscarPorId(id);
        return exame.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // PUT - Atualizar exame
    @PutMapping("/{id}")
    public ResponseEntity<ExameME> atualizarExame(@PathVariable Long id, @RequestBody ExameME exame) {
        try {
            ExameME exameAtualizado = exameService.atualizarExame(id, exame);
            return ResponseEntity.ok(exameAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST - Registrar resultado do exame
    @PostMapping("/{id}/resultado")
    public ResponseEntity<ExameME> registrarResultado(
            @PathVariable Long id,
            @RequestParam String resultado,
            @RequestParam(required = false) Boolean resultadoPositivo,
            @RequestParam(required = false) String responsavel) {
        try {
            ExameME exame = exameService.registrarResultado(id, resultado, resultadoPositivo, responsavel);
            return ResponseEntity.ok(exame);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET - Obter resumo dos exames
    @GetMapping("/protocolo/{protocoloId}/resumo")
    public ResponseEntity<ExameMEService.ExameResumo> obterResumo(@PathVariable Long protocoloId) {
        try {
            ExameMEService.ExameResumo resumo = exameService.obterResumoExames(protocoloId);
            return ResponseEntity.ok(resumo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE - Deletar exame
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarExame(@PathVariable Long id) {
        try {
            exameService.deletarExame(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
