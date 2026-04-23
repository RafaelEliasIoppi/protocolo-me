package back.backend.controller;

import back.backend.dto.ErrorResponseDTO;
import back.backend.dto.AnexoDocumentoDTO;
import back.backend.model.AnexoDocumento;
import back.backend.service.AnexoDocumentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/anexos")
@CrossOrigin(origins = "*")
public class AnexoDocumentoController {

    @Autowired
    private AnexoDocumentoService anexoService;

    /**
     * POST - Upload de arquivo para exame
     * /api/anexos/exame/{exameMEId}
     */
    @PostMapping("/exame/{exameMEId}")
    public ResponseEntity<?> uploadAnexoExame(
            @PathVariable Long exameMEId,
            @RequestParam("arquivo") MultipartFile arquivo,
            @RequestParam(value = "descricao", required = false) String descricao,
            @RequestParam(value = "uploadPor", required = false) String uploadPor) {
        try {
            AnexoDocumento anexo = anexoService.uploadAnexoExame(exameMEId, arquivo, descricao, uploadPor);
            return ResponseEntity.status(HttpStatus.CREATED).body(AnexoDocumentoDTO.fromEntity(anexo));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO("Erro ao fazer upload: " + e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }

    /**
     * POST - Upload de arquivo para entrevista familiar
     * /api/anexos/entrevista/{protocoloMEId}
     */
    @PostMapping("/entrevista/{protocoloMEId}")
    public ResponseEntity<?> uploadAnexoEntrevista(
            @PathVariable Long protocoloMEId,
            @RequestParam("arquivo") MultipartFile arquivo,
            @RequestParam(value = "descricao", required = false) String descricao,
            @RequestParam(value = "uploadPor", required = false) String uploadPor) {
        try {
            AnexoDocumento anexo = anexoService.uploadAnexoEntrevista(protocoloMEId, arquivo, descricao, uploadPor);
            return ResponseEntity.status(HttpStatus.CREATED).body(AnexoDocumentoDTO.fromEntity(anexo));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO("Erro ao fazer upload: " + e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }

    /**
     * GET - Listar anexos de um exame
     * /api/anexos/exame/{exameMEId}
     */
    @GetMapping("/exame/{exameMEId}")
    public ResponseEntity<List<AnexoDocumentoDTO>> listarAnexosExame(@PathVariable Long exameMEId) {
        try {
            List<AnexoDocumento> anexos = anexoService.listarAnexosExame(exameMEId);
            return ResponseEntity.ok(anexos.stream().map(AnexoDocumentoDTO::fromEntity).collect(Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET - Listar anexos de uma entrevista
     * /api/anexos/entrevista/{protocoloMEId}
     */
    @GetMapping("/entrevista/{protocoloMEId}")
    public ResponseEntity<List<AnexoDocumentoDTO>> listarAnexosEntrevista(@PathVariable Long protocoloMEId) {
        try {
            List<AnexoDocumento> anexos = anexoService.listarAnexosEntrevista(protocoloMEId);
            return ResponseEntity.ok(anexos.stream().map(AnexoDocumentoDTO::fromEntity).collect(Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET - Obter detalhes de um anexo
     * /api/anexos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obterAnexo(@PathVariable Long id) {
        Optional<AnexoDocumento> anexo = anexoService.obterAnexoPorId(id);
        return anexo.map(value -> ResponseEntity.ok(AnexoDocumentoDTO.fromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * GET - Download de arquivo
     * /api/anexos/{id}/download
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<?> downloadArquivo(@PathVariable Long id) {
        try {
            Optional<AnexoDocumento> anexoOpt = anexoService.obterAnexoPorId(id);
            if (anexoOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            AnexoDocumento anexo = anexoOpt.get();
            byte[] conteudo = anexoService.downloadArquivo(id);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(anexo.getTipoMime()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + anexo.getNomeArquivo() + "\"")
                    .body(conteudo);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponseDTO("Erro ao fazer download: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * DELETE - Deletar anexo
     * /api/anexos/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarAnexo(@PathVariable Long id) {
        try {
            anexoService.deletarAnexo(id);
            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponseDTO("Erro ao deletar anexo: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE - Deletar todos os anexos de um exame
     * /api/anexos/exame/{exameMEId}/limpar
     */
    @DeleteMapping("/exame/{exameMEId}/limpar")
    public ResponseEntity<?> limparAnexosExame(@PathVariable Long exameMEId) {
        try {
            anexoService.deletarAnexosExame(exameMEId);
            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponseDTO("Erro ao limpar anexos: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}
