package back.backend.controller;

import back.backend.dto.ErrorResponseDTO;
import back.backend.dto.ExameMEDTO;
import back.backend.dto.ExameResumoDTO;
import back.backend.mapper.ExameMapper;
import back.backend.mapper.ExameResumoMapper;
import back.backend.model.ExameME;
import back.backend.service.ExameMEService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/exames-me")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ExameMEController {

    private final ExameMEService exameService;
    private final ExameMapper exameMapper;
    private final ExameResumoMapper exameResumoMapper;

    // POST - Criar novo exame
    @PostMapping
    public ResponseEntity<ExameMEDTO> criarExame(@RequestBody ExameME exame) {
        try {
            ExameME novoExame = exameService.criarExame(exame);
            return ResponseEntity.status(HttpStatus.CREATED).body(exameMapper.toDTO(novoExame));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // GET - Listar exames de um protocolo
    @GetMapping("/protocolo/{protocoloId}")
    public ResponseEntity<List<ExameMEDTO>> listarExamePorProtocolo(@PathVariable Long protocoloId) {
        try {
            List<ExameME> exames = exameService.listarExamesPorProtocolo(protocoloId);
            return ResponseEntity.ok(exames.stream().map(exameMapper::toDTO).collect(Collectors.toList()));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET - Listar exames clínicos
    @GetMapping("/protocolo/{protocoloId}/clinicos")
    public ResponseEntity<List<ExameMEDTO>> listarExamesClinico(@PathVariable Long protocoloId) {
        try {
            List<ExameME> exames = exameService.listarExamesClinico(protocoloId);
            return ResponseEntity.ok(exames.stream().map(exameMapper::toDTO).collect(Collectors.toList()));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET - Listar exames complementares
    @GetMapping("/protocolo/{protocoloId}/complementares")
    public ResponseEntity<List<ExameMEDTO>> listarExamesComplementares(@PathVariable Long protocoloId) {
        try {
            List<ExameME> exames = exameService.listarExamesComplementares(protocoloId);
            return ResponseEntity.ok(exames.stream().map(exameMapper::toDTO).collect(Collectors.toList()));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET - Listar exames laboratoriais
    @GetMapping("/protocolo/{protocoloId}/laboratoriais")
    public ResponseEntity<List<ExameMEDTO>> listarExamesLaboratoriais(@PathVariable Long protocoloId) {
        try {
            List<ExameME> exames = exameService.listarExamesLaboratoriais(protocoloId);
            return ResponseEntity.ok(exames.stream().map(exameMapper::toDTO).collect(Collectors.toList()));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET - Buscar exame por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<ExameME> exame = exameService.buscarPorId(id);
        return exame.map(value -> ResponseEntity.ok(exameMapper.toDTO(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // PUT - Atualizar exame
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarExame(@PathVariable Long id, @RequestBody ExameME exame) {
        try {
            ExameME exameAtualizado = exameService.atualizarExame(id, exame);
            return ResponseEntity.ok(exameMapper.toDTO(exameAtualizado));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST - Registrar resultado do exame
    @PostMapping("/{id}/resultado")
    public ResponseEntity<?> registrarResultado(
            @PathVariable Long id,
            @RequestParam String resultado,
            @RequestParam(required = false) Boolean resultado_positivo,
            @RequestParam(required = false) String responsavel) {
        try {
            ExameME exame = exameService.registrarResultado(id, resultado, resultado_positivo, responsavel);
            return ResponseEntity.ok(exameMapper.toDTO(exame));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET - Obter resumo dos exames
    @GetMapping("/protocolo/{protocoloId}/resumo")
    public ResponseEntity<ExameResumoDTO> obterResumo(@PathVariable Long protocoloId) {
        try {
            ExameMEService.ExameResumo resumo = exameService.obterResumoExames(protocoloId);
            return ResponseEntity.ok(exameResumoMapper.toDTO(resumo));
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

    // POST - Criar exame com atualização automática de status (incremental)
    @PostMapping("/incrementar")
    public ResponseEntity<?> criarExameIncremental(@RequestBody ExameME exame) {
        try {
            ExameME novoExame = exameService.criarExame(exame);
            return ResponseEntity.status(HttpStatus.CREATED).body(exameMapper.toDTO(novoExame));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO("Erro ao criar exame: " + e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }
}
