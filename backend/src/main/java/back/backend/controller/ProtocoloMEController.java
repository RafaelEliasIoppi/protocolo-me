package back.backend.controller;

import back.backend.model.ProtocoloME;
import back.backend.service.ProtocoloMEService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/protocolos-me")
public class ProtocoloMEController {

    @Autowired
    private ProtocoloMEService protocoloService;

    // POST - Criar novo protocolo de ME
    @PostMapping
    public ResponseEntity<ProtocoloME> criarProtocolo(@RequestBody ProtocoloME protocolo) {
        try {
            ProtocoloME novoProtocolo = protocoloService.criarProtocolo(protocolo);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoProtocolo);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // GET - Listar todos os protocolos
    @GetMapping
    public ResponseEntity<List<ProtocoloME>> listarTodos() {
        List<ProtocoloME> protocolos = protocoloService.listarTodos();
        return ResponseEntity.ok(protocolos);
    }

    // GET - Buscar protocolo por ID
    @GetMapping("/{id}")
    public ResponseEntity<ProtocoloME> buscarPorId(@PathVariable Long id) {
        Optional<ProtocoloME> protocolo = protocoloService.buscarPorId(id);
        return protocolo.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET - Buscar por número do protocolo
    @GetMapping("/numero/{numeroProtocolo}")
    public ResponseEntity<ProtocoloME> buscarPorNumero(@PathVariable String numeroProtocolo) {
        Optional<ProtocoloME> protocolo = protocoloService.buscarPorNumeroProtocolo(numeroProtocolo);
        return protocolo.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET - Listar protocolos de uma central
    @GetMapping("/central/{centralId}")
    public ResponseEntity<List<ProtocoloME>> listarPorCentral(@PathVariable Long centralId) {
        try {
            List<ProtocoloME> protocolos = protocoloService.listarPorCentral(centralId);
            return ResponseEntity.ok(protocolos);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET - Listar por status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ProtocoloME>> listarPorStatus(@PathVariable String status) {
        try {
            ProtocoloME.StatusProtocoloME statusEnum = ProtocoloME.StatusProtocoloME.valueOf(status.toUpperCase());
            List<ProtocoloME> protocolos = protocoloService.listarPorStatus(statusEnum);
            return ResponseEntity.ok(protocolos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // GET - Listar por central e status
    @GetMapping("/central/{centralId}/status/{status}")
    public ResponseEntity<List<ProtocoloME>> listarPorCentralEStatus(
            @PathVariable Long centralId,
            @PathVariable String status) {
        try {
            ProtocoloME.StatusProtocoloME statusEnum = ProtocoloME.StatusProtocoloME.valueOf(status.toUpperCase());
            List<ProtocoloME> protocolos = protocoloService.listarPorCentralEStatus(centralId, statusEnum);
            return ResponseEntity.ok(protocolos);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET - Listar por período
    @GetMapping("/periodo")
    public ResponseEntity<List<ProtocoloME>> listarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {
        List<ProtocoloME> protocolos = protocoloService.listarPorPeriodo(dataInicio, dataFim);
        return ResponseEntity.ok(protocolos);
    }

    // GET - Listar por hospital origem
    @GetMapping("/hospital/{hospitalOrigem}")
    public ResponseEntity<List<ProtocoloME>> listarPorHospitalOrigem(@PathVariable String hospitalOrigem) {
        List<ProtocoloME> protocolos = protocoloService.listarPorHospitalOrigem(hospitalOrigem);
        return ResponseEntity.ok(protocolos);
    }

    // GET - Listar protocolos de um paciente
    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<ProtocoloME>> listarPorPaciente(@PathVariable Long pacienteId) {
        try {
            List<ProtocoloME> protocolos = protocoloService.listarPorPaciente(pacienteId);
            return ResponseEntity.ok(protocolos);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // PUT - Atualizar protocolo
    @PutMapping("/{id}")
    public ResponseEntity<ProtocoloME> atualizarProtocolo(@PathVariable Long id, @RequestBody ProtocoloME protocolo) {
        try {
            ProtocoloME protocoloAtualizado = protocoloService.atualizarProtocolo(id, protocolo);
            return ResponseEntity.ok(protocoloAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST - Registrar teste clínico 1
    @PostMapping("/{id}/teste-clinico-1")
    public ResponseEntity<ProtocoloME> registrarTesteClinico1(@PathVariable Long id) {
        try {
            ProtocoloME protocolo = protocoloService.registrarTesteClinico1(id);
            return ResponseEntity.ok(protocolo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST - Registrar teste clínico 2
    @PostMapping("/{id}/teste-clinico-2")
    public ResponseEntity<ProtocoloME> registrarTesteClinico2(@PathVariable Long id) {
        try {
            ProtocoloME protocolo = protocoloService.registrarTesteClinico2(id);
            return ResponseEntity.ok(protocolo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST - Registrar testes complementares
    @PostMapping("/{id}/testes-complementares")
    public ResponseEntity<ProtocoloME> registrarTestesComplementares(
            @PathVariable Long id,
            @RequestParam String testesComplementares) {
        try {
            ProtocoloME protocolo = protocoloService.registrarTestesComplementares(id, testesComplementares);
            return ResponseEntity.ok(protocolo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST - Registrar notificação da família
    @PostMapping("/{id}/notificar-familia")
    public ResponseEntity<ProtocoloME> registrarNotificacaoFamilia(@PathVariable Long id) {
        try {
            ProtocoloME protocolo = protocoloService.registrarNotificacaoFamilia(id);
            return ResponseEntity.ok(protocolo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST - Autorizar autópsia
    @PostMapping("/{id}/autorizar-autopsia")
    public ResponseEntity<ProtocoloME> autorizarAutopsia(@PathVariable Long id) {
        try {
            ProtocoloME protocolo = protocoloService.autorizarAutopsia(id);
            return ResponseEntity.ok(protocolo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST - Registrar preservação de órgãos
    @PostMapping("/{id}/preservacao-orgaos")
    public ResponseEntity<ProtocoloME> registrarPreservacaoOrgaos(@PathVariable Long id) {
        try {
            ProtocoloME protocolo = protocoloService.registrarPreservacaoOrgaos(id);
            return ResponseEntity.ok(protocolo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST - Confirmar morte cerebral
    @PostMapping("/{id}/confirmar-morte-cerebral")
    public ResponseEntity<ProtocoloME> confirmarMorteCerebral(@PathVariable Long id) {
        try {
            ProtocoloME protocolo = protocoloService.confirmarMorteCerebral(id);
            return ResponseEntity.ok(protocolo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // PATCH - Alterar status do protocolo
    @PatchMapping("/{id}/status")
    public ResponseEntity<ProtocoloME> alterarStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        try {
            ProtocoloME.StatusProtocoloME novoStatus = ProtocoloME.StatusProtocoloME.valueOf(status.toUpperCase());
            ProtocoloME protocolo = protocoloService.alterarStatus(id, novoStatus);
            return ResponseEntity.ok(protocolo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE - Deletar protocolo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProtocolo(@PathVariable Long id) {
        try {
            protocoloService.deletarProtocolo(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
