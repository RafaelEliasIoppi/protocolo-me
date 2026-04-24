package back.backend.controller;

import back.backend.dto.ProtocoloCreateRequestDTO;
import back.backend.dto.ProtocoloRelatorioRequestDTO;
import back.backend.dto.ProtocoloStatusRequestDTO;
import back.backend.dto.ProtocoloUpdateRequestDTO;
import back.backend.dto.ProtocoloMEDTO;
import back.backend.model.ProtocoloME;
import back.backend.mapper.ProtocoloMapper;
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
    private final ProtocoloMapper protocoloMapper;
    private final ProtocoloRequestMapper protocoloRequestMapper;

    // ================= CREATE =================

    @PostMapping
    public ResponseEntity<ProtocoloMEDTO> criarProtocolo(@Valid @RequestBody ProtocoloCreateRequestDTO request) {
        ProtocoloME novo = protocoloService.criarProtocoloPorPacienteId(
                request.getPacienteId(),
                request.getDiagnosticoBasico(),
                request.getNumeroProtocolo()
        );

        return ResponseEntity
                .status(201)
                .body(protocoloMapper.toDTO(novo));
    }

    // ================= READ =================

    @GetMapping
    public ResponseEntity<List<ProtocoloMEDTO>> listarTodos() {
        return ResponseEntity.ok(
                protocoloService.listarTodos()
                        .stream()
                .map(protocoloMapper::toDTO)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProtocoloMEDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(protocoloMapper.toDTO(protocoloService.buscarPorIdOuFalhar(id)));
    }

    @GetMapping("/numero/{numero}")
    public ResponseEntity<ProtocoloMEDTO> buscarPorNumero(@PathVariable String numero) {
        return ResponseEntity.ok(protocoloMapper.toDTO(protocoloService.buscarPorNumeroProtocoloOuFalhar(numero)));
    }

    @GetMapping("/central/{centralId}")
    public ResponseEntity<?> listarPorCentral(@PathVariable Long centralId) {
        return ResponseEntity.ok(
                protocoloService.listarPorCentral(centralId)
                        .stream()
                        .map(protocoloMapper::toDTO)
                        .toList()
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> listarPorStatus(@PathVariable String status) {
        ProtocoloME.StatusProtocoloME s = parseStatus(status);

        return ResponseEntity.ok(
                protocoloService.listarPorStatus(s)
                        .stream()
                        .map(protocoloMapper::toDTO)
                        .toList()
        );
    }

    @GetMapping("/central/{centralId}/status/{status}")
    public ResponseEntity<?> listarPorCentralEStatus(
            @PathVariable Long centralId,
            @PathVariable String status) {

        ProtocoloME.StatusProtocoloME s = parseStatus(status);

        return ResponseEntity.ok(
                protocoloService.listarPorCentralEStatus(centralId, s)
                        .stream()
                        .map(protocoloMapper::toDTO)
                        .toList()
        );
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<ProtocoloMEDTO>> listarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {

        return ResponseEntity.ok(
                protocoloService.listarPorPeriodo(inicio, fim)
                        .stream()
                .map(protocoloMapper::toDTO)
                        .toList()
        );
    }

    @GetMapping("/hospital/{hospital}")
    public ResponseEntity<List<ProtocoloMEDTO>> listarPorHospital(@PathVariable String hospital) {
        return ResponseEntity.ok(
                protocoloService.listarPorHospitalOrigem(hospital)
                        .stream()
                .map(protocoloMapper::toDTO)
                        .toList()
        );
    }

    // ================= UPDATE =================

    @PutMapping("/{id}")
    public ResponseEntity<ProtocoloMEDTO> atualizar(@PathVariable Long id, @RequestBody ProtocoloUpdateRequestDTO request) {
        ProtocoloME protocolo = protocoloRequestMapper.toEntity(request);
        return ResponseEntity.ok(
                protocoloMapper.toDTO(protocoloService.atualizarProtocolo(id, protocolo))
        );
    }

    @PatchMapping("/{id}/relatorio-final")
    public ResponseEntity<ProtocoloMEDTO> atualizarRelatorioFinal(
            @PathVariable Long id,
            @RequestBody ProtocoloRelatorioRequestDTO request) {

        return ResponseEntity.ok(
                protocoloMapper.toDTO(protocoloService.atualizarRelatorioFinal(
                        id,
                        request.getTextoRelatorio(),
                        request.getAtualizadoPor()
                ))
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ProtocoloMEDTO> alterarStatus(
            @PathVariable Long id,
            @RequestBody ProtocoloStatusRequestDTO request) {

        ProtocoloME.StatusProtocoloME s = parseStatus(request.getStatus());

        return ResponseEntity.ok(
                protocoloMapper.toDTO(protocoloService.alterarStatus(id, s))
        );
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

    private ResponseEntity<ProtocoloMEDTO> action(Long id, java.util.function.Function<Long, ProtocoloME> fn) {
        return ResponseEntity.ok(protocoloMapper.toDTO(fn.apply(id)));
    }
}