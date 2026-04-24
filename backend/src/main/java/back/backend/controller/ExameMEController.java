package back.backend.controller;

import back.backend.dto.ErrorResponseDTO;
import back.backend.dto.ExameMEDTO;
import back.backend.dto.ExameResumoDTO;
import back.backend.model.ExameME;
import back.backend.service.ExameMEService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exames-me")
@CrossOrigin(origins = "*")
public class ExameMEController {

    private final ExameMEService exameService;

    public ExameMEController(ExameMEService exameService) {
        this.exameService = exameService;
    }

    // POST - Criar novo exame
    @PostMapping
    public ResponseEntity<?> criarExame(@RequestBody ExameME exame) {
        try {
            ExameME novoExame = exameService.criarExame(exame);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ExameMEDTO.fromEntity(novoExame));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponseDTO(e.getMessage(), 400));
        }
    }

    // GET - Listar exames de um protocolo
    @GetMapping("/protocolo/{protocoloId}")
    public ResponseEntity<List<ExameMEDTO>> listarExamePorProtocolo(@PathVariable Long protocoloId) {
        List<ExameMEDTO> lista = exameService.listarExamesPorProtocolo(protocoloId)
                .stream()
                .map(ExameMEDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(lista);
    }

    // GET - Listar exames clínicos
    @GetMapping("/protocolo/{protocoloId}/clinicos")
    public ResponseEntity<List<ExameMEDTO>> listarExamesClinico(@PathVariable Long protocoloId) {
        return ResponseEntity.ok(
                exameService.listarExamesClinico(protocoloId)
                        .stream()
                        .map(ExameMEDTO::fromEntity)
                        .toList()
        );
    }

    // GET - Listar exames complementares
    @GetMapping("/protocolo/{protocoloId}/complementares")
    public ResponseEntity<List<ExameMEDTO>> listarExamesComplementares(@PathVariable Long protocoloId) {
        return ResponseEntity.ok(
                exameService.listarExamesComplementares(protocoloId)
                        .stream()
                        .map(ExameMEDTO::fromEntity)
                        .toList()
        );
    }

    // GET - Listar exames laboratoriais
    @GetMapping("/protocolo/{protocoloId}/laboratoriais")
    public ResponseEntity<List<ExameMEDTO>> listarExamesLaboratoriais(@PathVariable Long protocoloId) {
        return ResponseEntity.ok(
                exameService.listarExamesLaboratoriais(protocoloId)
                        .stream()
                        .map(ExameMEDTO::fromEntity)
                        .toList()
        );
    }

    // GET - Buscar exame por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        return exameService.buscarPorId(id)
                .map(e -> ResponseEntity.ok(ExameMEDTO.fromEntity(e)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponseDTO("Exame não encontrado", 404)));
    }

    // PUT - Atualizar exame
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarExame(@PathVariable Long id, @RequestBody ExameME exame) {
        try {
            return ResponseEntity.ok(
                    ExameMEDTO.fromEntity(exameService.atualizarExame(id, exame))
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(e.getMessage(), 404));
        }
    }

    // POST - Registrar resultado
    @PostMapping("/{id}/resultado")
    public ResponseEntity<?> registrarResultado(
            @PathVariable Long id,
            @RequestParam String resultado,
            @RequestParam(required = false) Boolean resultado_positivo,
            @RequestParam(required = false) String responsavel) {

        try {
            return ResponseEntity.ok(
                    ExameMEDTO.fromEntity(
                            exameService.registrarResultado(id, resultado, resultado_positivo, responsavel)
                    )
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(e.getMessage(), 404));
        }
    }

    // GET - Resumo
    @GetMapping("/protocolo/{protocoloId}/resumo")
    public ResponseEntity<?> obterResumo(@PathVariable Long protocoloId) {
        try {
            return ResponseEntity.ok(
                    ExameResumoDTO.fromService(
                            exameService.obterResumoExames(protocoloId)
                    )
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(e.getMessage(), 404));
        }
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarExame(@PathVariable Long id) {
        try {
            exameService.deletarExame(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(e.getMessage(), 404));
        }
    }

    // POST incremental
    @PostMapping("/incrementar")
    public ResponseEntity<?> criarExameIncremental(@RequestBody ExameME exame) {
        return criarExame(exame);
    }
}