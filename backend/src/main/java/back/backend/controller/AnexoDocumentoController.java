package back.backend.controller;

import back.backend.dto.AnexoDocumentoDTO;
import back.backend.model.AnexoDocumento;
import back.backend.service.AnexoDocumentoService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/anexos")
@CrossOrigin(origins = "*")
public class AnexoDocumentoController {

    private final AnexoDocumentoService anexoService;

    public AnexoDocumentoController(AnexoDocumentoService anexoService) {
        this.anexoService = anexoService;
    }

    // ---------------- UPLOAD EXAME ----------------

    @PostMapping("/exame/{exameMEId}")
    public ResponseEntity<AnexoDocumentoDTO> uploadExame(
            @PathVariable Long exameMEId,
            @RequestParam("arquivo") MultipartFile arquivo,
            @RequestParam(required = false) String descricao,
            @RequestParam(required = false) String uploadPor) throws IOException {

        AnexoDocumento anexo = anexoService.uploadAnexoExame(
                exameMEId, arquivo, descricao, uploadPor
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AnexoDocumentoDTO.fromEntity(anexo));
    }

    // ---------------- UPLOAD ENTREVISTA ----------------

    @PostMapping("/entrevista/{protocoloMEId}")
    public ResponseEntity<AnexoDocumentoDTO> uploadEntrevista(
            @PathVariable Long protocoloMEId,
            @RequestParam("arquivo") MultipartFile arquivo,
            @RequestParam(required = false) String descricao,
            @RequestParam(required = false) String uploadPor) throws IOException {

        AnexoDocumento anexo = anexoService.uploadAnexoEntrevista(
                protocoloMEId, arquivo, descricao, uploadPor
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AnexoDocumentoDTO.fromEntity(anexo));
    }

    // ---------------- LIST EXAME ----------------

    @GetMapping("/exame/{exameMEId}")
    public ResponseEntity<List<AnexoDocumentoDTO>> listarExame(@PathVariable Long exameMEId) {

        List<AnexoDocumentoDTO> lista = anexoService.listarAnexosExame(exameMEId)
                .stream()
                .map(AnexoDocumentoDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(lista);
    }

    // ---------------- LIST ENTREVISTA ----------------

    @GetMapping("/entrevista/{protocoloMEId}")
    public ResponseEntity<List<AnexoDocumentoDTO>> listarEntrevista(@PathVariable Long protocoloMEId) {

        List<AnexoDocumentoDTO> lista = anexoService.listarAnexosEntrevista(protocoloMEId)
                .stream()
                .map(AnexoDocumentoDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(lista);
    }

    // ---------------- DETAIL ----------------

    @GetMapping("/{id}")
    public ResponseEntity<AnexoDocumentoDTO> obter(@PathVariable Long id) {

        AnexoDocumento anexo = anexoService.obterPorId(id);

        return ResponseEntity.ok(AnexoDocumentoDTO.fromEntity(anexo));
    }

    // ---------------- DOWNLOAD ----------------

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable Long id) throws IOException {

        AnexoDocumento anexo = anexoService.obterPorId(id);
        byte[] conteudo = anexoService.downloadArquivo(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(anexo.getTipoMime()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + anexo.getNomeArquivo() + "\"")
                .body(conteudo);
    }

    // ---------------- DELETE ----------------

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) throws IOException {

        anexoService.deletarAnexo(id);

        return ResponseEntity.noContent().build();
    }

    // ---------------- CLEAN EXAME ----------------

    @DeleteMapping("/exame/{exameMEId}/limpar")
    public ResponseEntity<Void> limparExame(@PathVariable Long exameMEId) throws IOException {

        anexoService.deletarAnexosExame(exameMEId);

        return ResponseEntity.noContent().build();
    }
}