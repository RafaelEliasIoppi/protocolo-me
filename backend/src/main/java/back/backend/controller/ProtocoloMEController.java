package back.backend.controller;

import back.backend.dto.ProtocoloCreateRequestDTO;
import back.backend.dto.ProtocoloRelatorioRequestDTO;
import back.backend.dto.ProtocoloStatusRequestDTO;
import back.backend.dto.ProtocoloUpdateRequestDTO;
import back.backend.dto.ProtocoloMEDTO;
import back.backend.model.ProtocoloME;
import back.backend.mapper.ProtocoloRequestMapper;
import back.backend.service.ProtocoloMEService;
import lombok.RequiredArgsConstructor;
import javax.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/protocolos-me")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ProtocoloMEController {

    private final ProtocoloMEService protocoloService;
    private final ProtocoloRequestMapper protocoloRequestMapper;

    // ================= CREATE =================

    @PostMapping
    public ResponseEntity<ProtocoloMEDTO> criarProtocolo(@Valid @RequestBody ProtocoloCreateRequestDTO request) {
        return ResponseEntity
            .status(201)
            .body(protocoloService.criarProtocoloPorPacienteId(
                request.getPacienteId(),
                request.getDiagnosticoBasico(),
                request.getNumeroProtocolo()
        ));
    }

    // ================= READ =================

    @GetMapping
    public ResponseEntity<List<ProtocoloMEDTO>> listarTodos() {
        return ResponseEntity.ok(protocoloService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProtocoloMEDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(protocoloService.buscarPorIdOuFalhar(id));
    }

    @GetMapping("/numero/{numero}")
    public ResponseEntity<ProtocoloMEDTO> buscarPorNumero(@PathVariable String numero) {
        return ResponseEntity.ok(protocoloService.buscarPorNumeroProtocoloOuFalhar(numero));
    }

    @GetMapping("/central/{centralId}")
    public ResponseEntity<?> listarPorCentral(@PathVariable Long centralId) {
        return ResponseEntity.ok(protocoloService.listarPorCentral(centralId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> listarPorStatus(@PathVariable String status) {
        ProtocoloME.StatusProtocoloME s = parseStatus(status);

        return ResponseEntity.ok(protocoloService.listarPorStatus(s));
    }

    @GetMapping("/central/{centralId}/status/{status}")
    public ResponseEntity<?> listarPorCentralEStatus(
            @PathVariable Long centralId,
            @PathVariable String status) {

        ProtocoloME.StatusProtocoloME s = parseStatus(status);

        return ResponseEntity.ok(protocoloService.listarPorCentralEStatus(centralId, s));
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<ProtocoloMEDTO>> listarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {

        return ResponseEntity.ok(protocoloService.listarPorPeriodo(inicio, fim));
    }

    @GetMapping("/hospital/{hospital}")
    public ResponseEntity<List<ProtocoloMEDTO>> listarPorHospital(@PathVariable String hospital) {
        return ResponseEntity.ok(protocoloService.listarPorHospitalOrigem(hospital));
    }

    // ================= UPDATE =================

    @PutMapping("/{id}")
    public ResponseEntity<ProtocoloMEDTO> atualizar(@PathVariable Long id, @RequestBody ProtocoloUpdateRequestDTO request) {
        ProtocoloME protocolo = protocoloRequestMapper.toEntity(request);
        return ResponseEntity.ok(protocoloService.atualizarProtocolo(id, protocolo));
    }

    @PatchMapping("/{id}/relatorio-final")
    public ResponseEntity<ProtocoloMEDTO> atualizarRelatorioFinal(
            @PathVariable Long id,
            @RequestBody ProtocoloRelatorioRequestDTO request) {

        return ResponseEntity.ok(
            protocoloService.atualizarRelatorioFinal(
                    id,
                    request.getTextoRelatorio(),
                    request.getAtualizadoPor()
            )
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ProtocoloMEDTO> alterarStatus(
            @PathVariable Long id,
            @RequestBody ProtocoloStatusRequestDTO request) {

        ProtocoloME.StatusProtocoloME s = parseStatus(request.getStatus());

        return ResponseEntity.ok(protocoloService.alterarStatus(id, s));
    }

    // ================= ACTIONS =================

    @PostMapping("/{id}/teste-clinico-1")
    public ResponseEntity<ProtocoloMEDTO> teste1(@PathVariable Long id) {
        return action(id, protocoloService::registrarTesteClinico1);
    }

    @PostMapping("/{id}/teste-clinico-2")
    public ResponseEntity<ProtocoloMEDTO> teste2(@PathVariable Long id) {
        return action(id, protocoloService::registrarTesteClinico2);
    }

    @PostMapping("/{id}/confirmar-morte-cerebral")
    public ResponseEntity<ProtocoloMEDTO> confirmar(@PathVariable Long id) {
        return action(id, protocoloService::confirmarMorteCerebral);
    }

    @PostMapping("/{id}/notificar-familia")
    public ResponseEntity<ProtocoloMEDTO> notificar(@PathVariable Long id) {
        return action(id, protocoloService::registrarNotificacaoFamilia);
    }

    @PostMapping("/{id}/autorizar-autopsia")
    public ResponseEntity<ProtocoloMEDTO> autopsia(@PathVariable Long id) {
        return action(id, protocoloService::autorizarAutopsia);
    }

    @PostMapping("/{id}/preservacao-orgaos")
    public ResponseEntity<ProtocoloMEDTO> preservar(@PathVariable Long id) {
        return action(id, protocoloService::registrarPreservacaoOrgaos);
    }

    @PostMapping("/{id}/atualizar-status")
    public ResponseEntity<ProtocoloMEDTO> atualizarStatusAuto(@PathVariable Long id) {
        return action(id, protocoloService::atualizarStatusAutomatico);
    }

    // ================= DELETE =================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        protocoloService.deletarProtocolo(id);
        return ResponseEntity.noContent().build();
    }

    // ================= HELPERS =================

    private ProtocoloME.StatusProtocoloME parseStatus(String status) {
        return ProtocoloME.StatusProtocoloME.valueOf(status.toUpperCase());
    }

    private ResponseEntity<ProtocoloMEDTO> action(Long id, java.util.function.Function<Long, ProtocoloMEDTO> fn) {
        return ResponseEntity.ok(fn.apply(id));
    }
}