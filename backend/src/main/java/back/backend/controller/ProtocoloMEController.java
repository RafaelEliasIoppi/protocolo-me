package back.backend.controller;

import back.backend.dto.ErrorResponseDTO;
import back.backend.dto.ProtocoloMEDTO;
import back.backend.model.ProtocoloME;
import back.backend.service.ProtocoloMEService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/protocolos-me")
@CrossOrigin(origins = "*")
public class ProtocoloMEController {

    @Autowired
    private ProtocoloMEService protocoloService;

    // ================= CREATE =================

    @PostMapping
    public ResponseEntity<?> criarProtocolo(@RequestBody ProtocoloCreateRequest request) {
        try {
            ProtocoloME novo = protocoloService.criarProtocoloPorPacienteId(
                    request.getPacienteId(),
                    request.getDiagnosticoBasico(),
                    request.getNumeroProtocolo()
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ProtocoloMEDTO.fromEntity(novo));

        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        }
    }

    // ================= READ =================

    @GetMapping
    public ResponseEntity<List<ProtocoloMEDTO>> listarTodos() {
        return ResponseEntity.ok(
                protocoloService.listarTodos()
                        .stream()
                        .map(ProtocoloMEDTO::fromEntity)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        return protocoloService.buscarPorId(id)
                .map(ProtocoloMEDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/numero/{numero}")
    public ResponseEntity<?> buscarPorNumero(@PathVariable String numero) {
        return protocoloService.buscarPorNumeroProtocolo(numero)
                .map(ProtocoloMEDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/central/{centralId}")
    public ResponseEntity<?> listarPorCentral(@PathVariable Long centralId) {
        try {
            return ResponseEntity.ok(
                    protocoloService.listarPorCentral(centralId)
                            .stream()
                            .map(ProtocoloMEDTO::fromEntity)
                            .toList()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> listarPorStatus(@PathVariable String status) {
        try {
            ProtocoloME.StatusProtocoloME s = parseStatus(status);

            return ResponseEntity.ok(
                    protocoloService.listarPorStatus(s)
                            .stream()
                            .map(ProtocoloMEDTO::fromEntity)
                            .toList()
            );

        } catch (IllegalArgumentException e) {
            return badRequest("Status inválido");
        }
    }

    @GetMapping("/central/{centralId}/status/{status}")
    public ResponseEntity<?> listarPorCentralEStatus(
            @PathVariable Long centralId,
            @PathVariable String status) {

        try {
            ProtocoloME.StatusProtocoloME s = parseStatus(status);

            return ResponseEntity.ok(
                    protocoloService.listarPorCentralEStatus(centralId, s)
                            .stream()
                            .map(ProtocoloMEDTO::fromEntity)
                            .toList()
            );

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<ProtocoloMEDTO>> listarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {

        return ResponseEntity.ok(
                protocoloService.listarPorPeriodo(inicio, fim)
                        .stream()
                        .map(ProtocoloMEDTO::fromEntity)
                        .toList()
        );
    }

    @GetMapping("/hospital/{hospital}")
    public ResponseEntity<List<ProtocoloMEDTO>> listarPorHospital(@PathVariable String hospital) {
        return ResponseEntity.ok(
                protocoloService.listarPorHospitalOrigem(hospital)
                        .stream()
                        .map(ProtocoloMEDTO::fromEntity)
                        .toList()
        );
    }

    // ================= UPDATE =================

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody ProtocoloME protocolo) {
        try {
            return ResponseEntity.ok(
                    ProtocoloMEDTO.fromEntity(
                            protocoloService.atualizarProtocolo(id, protocolo)
                    )
            );
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/relatorio-final")
    public ResponseEntity<?> atualizarRelatorioFinal(
            @PathVariable Long id,
            @RequestBody RelatorioRequest request) {

        try {
            return ResponseEntity.ok(
                    ProtocoloMEDTO.fromEntity(
                            protocoloService.atualizarRelatorioFinal(
                                    id,
                                    request.getTextoRelatorio(),
                                    request.getAtualizadoPor()
                            )
                    )
            );

        } catch (RuntimeException e) {
            return badRequest(e.getMessage());
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> alterarStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        try {
            ProtocoloME.StatusProtocoloME s = parseStatus(status);

            return ResponseEntity.ok(
                    ProtocoloMEDTO.fromEntity(
                            protocoloService.alterarStatus(id, s)
                    )
            );

        } catch (IllegalArgumentException e) {
            return badRequest("Status inválido");
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ================= ACTIONS =================

    @PostMapping("/{id}/teste-clinico-1")
    public ResponseEntity<?> teste1(@PathVariable Long id) {
        return action(id, protocoloService::registrarTesteClinico1);
    }

    @PostMapping("/{id}/teste-clinico-2")
    public ResponseEntity<?> teste2(@PathVariable Long id) {
        return action(id, protocoloService::registrarTesteClinico2);
    }

    @PostMapping("/{id}/confirmar-morte-cerebral")
    public ResponseEntity<?> confirmar(@PathVariable Long id) {
        return action(id, protocoloService::confirmarMorteCerebral);
    }

    @PostMapping("/{id}/notificar-familia")
    public ResponseEntity<?> notificar(@PathVariable Long id) {
        return action(id, protocoloService::registrarNotificacaoFamilia);
    }

    @PostMapping("/{id}/autorizar-autopsia")
    public ResponseEntity<?> autopsia(@PathVariable Long id) {
        return action(id, protocoloService::autorizarAutopsia);
    }

    @PostMapping("/{id}/preservacao-orgaos")
    public ResponseEntity<?> preservar(@PathVariable Long id) {
        return action(id, protocoloService::registrarPreservacaoOrgaos);
    }

    @PostMapping("/{id}/atualizar-status")
    public ResponseEntity<?> atualizarStatusAuto(@PathVariable Long id) {
        return action(id, protocoloService::atualizarStatusAutomatico);
    }

    // ================= DELETE =================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            protocoloService.deletarProtocolo(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ================= HELPERS =================

    private ProtocoloME.StatusProtocoloME parseStatus(String status) {
        return ProtocoloME.StatusProtocoloME.valueOf(status.toUpperCase());
    }

    private ResponseEntity<ErrorResponseDTO> badRequest(String msg) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponseDTO(msg, 400));
    }

    private ResponseEntity<?> action(Long id, java.util.function.Function<Long, ProtocoloME> fn) {
        try {
            return ResponseEntity.ok(
                    ProtocoloMEDTO.fromEntity(fn.apply(id))
            );
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ================= DTOs INTERNOS =================

    public static class ProtocoloCreateRequest {
        private Long pacienteId;
        private String diagnosticoBasico;
        private String numeroProtocolo;

        public Long getPacienteId() { return pacienteId; }
        public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }

        public String getDiagnosticoBasico() { return diagnosticoBasico; }
        public void setDiagnosticoBasico(String diagnosticoBasico) { this.diagnosticoBasico = diagnosticoBasico; }

        public String getNumeroProtocolo() { return numeroProtocolo; }
        public void setNumeroProtocolo(String numeroProtocolo) { this.numeroProtocolo = numeroProtocolo; }
    }

    public static class RelatorioRequest {
        private String textoRelatorio;
        private String atualizadoPor;

        public String getTextoRelatorio() { return textoRelatorio; }
        public void setTextoRelatorio(String textoRelatorio) { this.textoRelatorio = textoRelatorio; }

        public String getAtualizadoPor() { return atualizadoPor; }
        public void setAtualizadoPor(String atualizadoPor) { this.atualizadoPor = atualizadoPor; }
    }
}