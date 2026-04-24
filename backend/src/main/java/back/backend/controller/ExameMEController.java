package back.backend.controller;

import back.backend.dto.ErrorResponseDTO;
import back.backend.dto.ExameMEDTO;
import back.backend.dto.ExameResumoDTO;
import back.backend.dto.ExameRequestDTO;
import back.backend.mapper.ExameMapper;
import back.backend.mapper.ExameRequestMapper;
import back.backend.mapper.ExameResumoMapper;
import back.backend.model.ExameME;
import back.backend.service.ExameMEService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/exames-me")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ExameMEController {

    private final ExameMEService exameService;
    private final ExameMapper exameMapper;
    private final ExameResumoMapper exameResumoMapper;
    private final ExameRequestMapper exameRequestMapper;

    // POST - Criar novo exame
    @PostMapping
    public ResponseEntity<ExameMEDTO> criarExame(@Valid @RequestBody ExameRequestDTO request) {
        var exame = exameRequestMapper.toEntity(request);
        ExameME novoExame = exameService.criarExame(exame);
        return ResponseEntity.status(HttpStatus.CREATED).body(exameMapper.toDTO(novoExame));
    }

    // GET - Listar exames de um protocolo
    @GetMapping("/protocolo/{protocoloId}")
    public ResponseEntity<List<ExameMEDTO>> listarExamePorProtocolo(@PathVariable Long protocoloId) {
        List<ExameME> exames = exameService.listarExamesPorProtocolo(protocoloId);
        return ResponseEntity.ok(exames.stream().map(exameMapper::toDTO).collect(Collectors.toList()));
    }

    // GET - Listar exames clínicos
    @GetMapping("/protocolo/{protocoloId}/clinicos")
    public ResponseEntity<List<ExameMEDTO>> listarExamesClinico(@PathVariable Long protocoloId) {
        List<ExameME> exames = exameService.listarExamesClinico(protocoloId);
        return ResponseEntity.ok(exames.stream().map(exameMapper::toDTO).collect(Collectors.toList()));
    }

    // GET - Listar exames complementares
    @GetMapping("/protocolo/{protocoloId}/complementares")
    public ResponseEntity<List<ExameMEDTO>> listarExamesComplementares(@PathVariable Long protocoloId) {
        List<ExameME> exames = exameService.listarExamesComplementares(protocoloId);
        return ResponseEntity.ok(exames.stream().map(exameMapper::toDTO).collect(Collectors.toList()));
    }

    // GET - Listar exames laboratoriais
    @GetMapping("/protocolo/{protocoloId}/laboratoriais")
    public ResponseEntity<List<ExameMEDTO>> listarExamesLaboratoriais(@PathVariable Long protocoloId) {
        List<ExameME> exames = exameService.listarExamesLaboratoriais(protocoloId);
        return ResponseEntity.ok(exames.stream().map(exameMapper::toDTO).collect(Collectors.toList()));
    }

    // GET - Buscar exame por ID
    @GetMapping("/{id}")
    public ResponseEntity<ExameMEDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(exameMapper.toDTO(exameService.buscarPorIdOuFalhar(id)));
    }

    // PUT - Atualizar exame
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarExame(@PathVariable Long id, @Valid @RequestBody ExameRequestDTO request) {
        var exame = exameRequestMapper.toEntity(request);
        ExameME exameAtualizado = exameService.atualizarExame(id, exame);
        return ResponseEntity.ok(exameMapper.toDTO(exameAtualizado));
    }

    // POST - Registrar resultado do exame
    @PostMapping("/{id}/resultado")
    public ResponseEntity<?> registrarResultado(
            @PathVariable Long id,
            @RequestParam String resultado,
            @RequestParam(required = false) Boolean resultado_positivo,
            @RequestParam(required = false) String responsavel) {
        ExameME exame = exameService.registrarResultado(id, resultado, resultado_positivo, responsavel);
        return ResponseEntity.ok(exameMapper.toDTO(exame));
    }

    // GET - Obter resumo dos exames
    @GetMapping("/protocolo/{protocoloId}/resumo")
    public ResponseEntity<ExameResumoDTO> obterResumo(@PathVariable Long protocoloId) {
        ExameMEService.ExameResumo resumo = exameService.obterResumoExames(protocoloId);
        return ResponseEntity.ok(exameResumoMapper.toDTO(resumo));
    }

    // DELETE - Deletar exame
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarExame(@PathVariable Long id) {
        exameService.deletarExame(id);
        return ResponseEntity.noContent().build();
    }

    // POST - Criar exame com atualização automática de status (incremental)
    @PostMapping("/incrementar")
    public ResponseEntity<?> criarExameIncremental(@Valid @RequestBody ExameRequestDTO request) {
        var exame = exameRequestMapper.toEntity(request);
        ExameME novoExame = exameService.criarExame(exame);
        return ResponseEntity.status(HttpStatus.CREATED).body(exameMapper.toDTO(novoExame));
    }
}
