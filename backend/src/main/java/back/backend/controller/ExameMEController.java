package back.backend.controller;

import back.backend.dto.ExameMEDTO;
import back.backend.dto.ExameResumoDTO;
import back.backend.dto.ExameRequestDTO;
import back.backend.mapper.ExameRequestMapper;
import back.backend.service.ExameMEService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/exames-me")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ExameMEController {

    private final ExameMEService exameService;
    private final ExameRequestMapper exameRequestMapper;

    // POST - Criar novo exame
    @PostMapping
    public ResponseEntity<ExameMEDTO> criarExame(@Valid @RequestBody ExameRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(exameService.criarExame(exameRequestMapper.toEntity(request)));
    }

    // GET - Listar exames de um protocolo
    @GetMapping("/protocolo/{protocoloId}")
    public ResponseEntity<List<ExameMEDTO>> listarExamePorProtocolo(@PathVariable Long protocoloId) {
        return ResponseEntity.ok(exameService.listarExamesPorProtocolo(protocoloId));
    }

    // GET - Listar exames clínicos
    @GetMapping("/protocolo/{protocoloId}/clinicos")
    public ResponseEntity<List<ExameMEDTO>> listarExamesClinico(@PathVariable Long protocoloId) {
        return ResponseEntity.ok(exameService.listarExamesClinico(protocoloId));
    }

    // GET - Listar exames complementares
    @GetMapping("/protocolo/{protocoloId}/complementares")
    public ResponseEntity<List<ExameMEDTO>> listarExamesComplementares(@PathVariable Long protocoloId) {
        return ResponseEntity.ok(exameService.listarExamesComplementares(protocoloId));
    }

    // GET - Listar exames laboratoriais
    @GetMapping("/protocolo/{protocoloId}/laboratoriais")
    public ResponseEntity<List<ExameMEDTO>> listarExamesLaboratoriais(@PathVariable Long protocoloId) {
        return ResponseEntity.ok(exameService.listarExamesLaboratoriais(protocoloId));
    }

    // GET - Buscar exame por ID
    @GetMapping("/{id}")
    public ResponseEntity<ExameMEDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(exameService.buscarPorIdOuFalhar(id));
    }

    // PUT - Atualizar exame
    @PutMapping("/{id}")
    public ResponseEntity<ExameMEDTO> atualizarExame(@PathVariable Long id, @Valid @RequestBody ExameRequestDTO request) {
        return ResponseEntity.ok(exameService.atualizarExame(id, exameRequestMapper.toEntity(request)));
    }

    // POST - Registrar resultado do exame
    @PostMapping("/{id}/resultado")
    public ResponseEntity<ExameMEDTO> registrarResultado(
            @PathVariable Long id,
            @RequestParam String resultado,
            @RequestParam(required = false) Boolean resultado_positivo,
            @RequestParam(required = false) String responsavel) {
        return ResponseEntity.ok(exameService.registrarResultado(id, resultado, resultado_positivo, responsavel));
    }

    // GET - Obter resumo dos exames
    @GetMapping("/protocolo/{protocoloId}/resumo")
    public ResponseEntity<ExameResumoDTO> obterResumo(@PathVariable Long protocoloId) {
        return ResponseEntity.ok(exameService.obterResumoExames(protocoloId));
    }

    // DELETE - Deletar exame
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarExame(@PathVariable Long id) {
        exameService.deletarExame(id);
        return ResponseEntity.noContent().build();
    }


}
